all:

.PHONY: test
test:
	clojure -M:dev:test

.PHONY: format
format:
	nix fmt

.PHONY: build
build: lock
	# nix build
	clojure -X visual-document.core/export

.PHONY: lock
lock: deps-lock.json

deps-lock.json: deps.edn flake.nix
	nix run github:jlesquembre/clj-nix#deps-lock -- --deps-include $<

.PHONY: dev
dev:
	clojure -X visual-document.core/start-server

.PHONY: deploy
deploy:
	npx wrangler pages deploy

generated/spectrum resources/spectrum:
	mkdir -p $@

resources/spectrum/%.json: resources/spectrum
	curl -L https://raw.githubusercontent.com/adobe/spectrum-tokens/refs/heads/main/packages/tokens/src/$*.json > $@

generated/spectrum/%.css: generated/spectrum
	curl -L https://raw.githubusercontent.com/conao3/blog-stasis-src/refs/heads/master/$@ > $@

SPECTRUM_FILE += color-aliases.json
SPECTRUM_FILE += color-component.json
SPECTRUM_FILE += color-palette.json
SPECTRUM_FILE += icons.json
SPECTRUM_FILE += layout-component.json
SPECTRUM_FILE += layout.json
SPECTRUM_FILE += semantic-color-palette.json
SPECTRUM_FILE += typography.json

.PHONY: fetch
fetch: $(SPECTRUM_FILE:%=resources/spectrum/%) $(SPECTRUM_FILE:%.json=generated/spectrum/%.css)
