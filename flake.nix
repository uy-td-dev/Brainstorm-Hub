
{
  description = "Dev environment for React+TS+Tailwind, Java 21, Kotlin, Docker";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
      in
      {
        devShells.default = pkgs.mkShell {
          name = "full-dev-shell";

          buildInputs = [
            # React + TypeScript + Tailwind (via Node.js)
            pkgs.nodejs_20
            pkgs.nodePackages.npm
            pkgs.nodePackages.yarn

            # Java 21 + Kotlin
            pkgs.openjdk21
            pkgs.kotlin

            # Docker & Docker Compose
            pkgs.docker
            pkgs.docker-compose

            # Optional: Useful CLI tools
            pkgs.git
            pkgs.jq
            pkgs.wget
            pkgs.curl
          ];

          shellHook = ''
            echo "✅ Dev shell ready for:"
            echo "   - React + TypeScript + Tailwind"
            echo "   - Java 21 + Kotlin"
            echo "   - Docker + Compose"
            echo ""

            # Optional: enable Docker for non-root if needed
            if ! groups | grep -q docker; then
              echo "⚠️ You may need to run: sudo usermod -aG docker $USER && newgrp docker"
            fi
          '';
        };
      });
}
