# ai-proxy

Server-side proxy that holds the OpenRouter API key and forwards chat-completion
requests for authenticated Synapse users. The Android app must **not** embed the
OpenRouter key (it is extractable from the APK); it lives here as a secret.

## Deploy

From a directory linked to your Supabase project (`supabase link --project-ref <ref>`):

```bash
# 1. Set the secret (do this once; it is never shipped in the app)
supabase secrets set OPENROUTER_API_KEY=sk-or-...

# 2. Deploy the function
supabase functions deploy ai-proxy
```

`SUPABASE_URL` and `SUPABASE_ANON_KEY` are injected automatically by the Edge
runtime — you do not set those.

## Contract

`POST {SUPABASE_URL}/functions/v1/ai-proxy`

Headers:
- `Authorization: Bearer <supabase-jwt>`
- `Content-Type: application/json`

Body (same shape the app already builds for OpenRouter):

```json
{ "model": "deepseek/deepseek-v4-flash", "max_tokens": 512, "messages": [ ... ] }
```

Response: OpenRouter's JSON verbatim (`usage`, `choices`, or `error`), so the
app's existing parser is unchanged.

## Hardening already applied
- Rejects requests without a valid Supabase JWT (`getUser`).
- Allowlists `model` (mirror of `SynapseModel.kt`) — no arbitrary models.
- Clamps `max_tokens` to `1..8192` server-side (does not trust the client).

## Extension point
The marked `(Optional) server-side energy/quota gate` in `index.ts` is where you
enforce remaining energy per `user.id` so a modified client cannot bypass the
quota. Return `{ "error": { "message": "..." } }` with a 402/403 to surface it
in the app's existing error UI.
