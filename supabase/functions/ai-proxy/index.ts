// ─────────────────────────────────────────────────────────────────────────────
// ai-proxy — Server-side proxy for OpenRouter chat completions.
//
// WHY THIS EXISTS
//   The Android keyboard must NOT ship the OpenRouter API key — anyone can
//   extract it from the APK and drain the account. This function holds the key
//   as a server secret and forwards requests on behalf of authenticated users.
//
// CONTRACT (kept identical to a direct OpenRouter call, so the app is unchanged)
//   Request  (POST, Authorization: Bearer <supabase-jwt>):
//     { "model": string, "max_tokens": number, "messages": [...] }
//   Response:
//     OpenRouter's JSON verbatim — the app keeps reading `usage`,
//     `choices[0].message.content`, and `error.message` exactly as before.
//
// SECRET REQUIRED (set once, never shipped in the app):
//   supabase secrets set OPENROUTER_API_KEY=sk-or-...
// ─────────────────────────────────────────────────────────────────────────────

import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

// Models the app is allowed to request. Mirror SynapseModel.kt `primaryModelId`
// so an authenticated user can't smuggle in an arbitrary (expensive) model.
const ALLOWED_MODELS = new Set<string>([
  "deepseek/deepseek-v4-flash",
  "xiaomi/mimo-v2.5",
]);

// Defence-in-depth: clamp output tokens server-side too (the app already caps
// this, but the server must not trust the client value).
const MAX_OUTPUT_TOKENS = 8192;
const MIN_OUTPUT_TOKENS = 1;

const JSON_HEADERS = { "Content-Type": "application/json" } as const;

function err(message: string, status: number): Response {
  // Shape matches OpenRouter's `{ error: { message } }` so the app's existing
  // error parser surfaces a useful message unchanged.
  return new Response(JSON.stringify({ error: { message } }), {
    status,
    headers: JSON_HEADERS,
  });
}

Deno.serve(async (req: Request): Promise<Response> => {
  if (req.method !== "POST") return err("Method not allowed", 405);

  // ── 1. Authenticate the caller via their Supabase JWT ──────────────────────
  const authHeader = req.headers.get("Authorization") ?? "";
  if (!authHeader.startsWith("Bearer ")) return err("Missing bearer token", 401);

  const supabase = createClient(
    Deno.env.get("SUPABASE_URL")!,
    Deno.env.get("SUPABASE_ANON_KEY")!,
    { global: { headers: { Authorization: authHeader } } },
  );

  const { data: { user }, error: userErr } = await supabase.auth.getUser();
  if (userErr || !user) return err("Invalid or expired session", 401);

  // ── 2. Parse & validate the request body ───────────────────────────────────
  let payload: { model?: unknown; max_tokens?: unknown; messages?: unknown };
  try {
    payload = await req.json();
  } catch {
    return err("Invalid JSON body", 400);
  }

  const model = typeof payload.model === "string" ? payload.model : "";
  if (!ALLOWED_MODELS.has(model)) return err("Unsupported model", 400);

  const messages = payload.messages;
  if (!Array.isArray(messages) || messages.length === 0) {
    return err("`messages` is required", 400);
  }

  const requested = Number(payload.max_tokens);
  const maxTokens = Math.min(
    Math.max(Number.isFinite(requested) ? Math.trunc(requested) : MIN_OUTPUT_TOKENS, MIN_OUTPUT_TOKENS),
    MAX_OUTPUT_TOKENS,
  );

  // ── (Optional) server-side energy/quota gate ───────────────────────────────
  // This is the natural place to enforce the user's remaining energy so a
  // modified client can't bypass it. Keyed on `user.id`. Left as an extension
  // point — wiring it requires your energy table schema. Returning here with a
  // 402/403 + `{ error: { message } }` is already handled by the app's UI.

  // ── 3. Forward to OpenRouter with the server-held key ──────────────────────
  const apiKey = Deno.env.get("OPENROUTER_API_KEY");
  if (!apiKey) return err("Server misconfigured: missing API key", 500);

  let upstream: Response;
  try {
    upstream = await fetch(OPENROUTER_URL, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${apiKey}`,
        "Content-Type": "application/json",
        "HTTP-Referer": "https://synapsekeyboard.app",
        "X-Title": "Synapse AI Keyboard",
      },
      body: JSON.stringify({ model, max_tokens: maxTokens, messages }),
    });
  } catch (_e) {
    return err("Upstream request failed", 502);
  }

  // Return OpenRouter's response verbatim (status + body) so the app's existing
  // parser — usage / choices[0].message.content / error.message — is unchanged.
  const body = await upstream.text();
  return new Response(body, { status: upstream.status, headers: JSON_HEADERS });
});
