# Changelog

## [5.1.0](https://github.com/workos/workos-kotlin/compare/v5.0.0...v5.1.0) (2026-05-11)


### Features

* publish Dokka API docs to GitHub Pages ([#379](https://github.com/workos/workos-kotlin/issues/379)) ([8dbce54](https://github.com/workos/workos-kotlin/commit/8dbce542d61fbaa7332d6ce7f0cc806ca987be4f))


### Bug Fixes

* **deps:** update minor and patch updates ([#358](https://github.com/workos/workos-kotlin/issues/358)) ([74a45ff](https://github.com/workos/workos-kotlin/commit/74a45fff86e1b69fa562d8bea493a8ed6b6d048e))
* harden URL/token handling and webhook timestamp validation ([#380](https://github.com/workos/workos-kotlin/issues/380)) ([e10f2a3](https://github.com/workos/workos-kotlin/commit/e10f2a3d2a0f20378fb91b1fd2d30a75203317e5))
* mask each line of decoded signing key in release workflow ([0aa1609](https://github.com/workos/workos-kotlin/commit/0aa1609c868455af6ca9f561064f7b69bed5c546))

## [5.0.0](https://github.com/workos/workos-kotlin/compare/v4.25.0...v5.0.0) (2026-05-06)


### ⚠ BREAKING CHANGES

* Release the next major SDK version ([#357](https://github.com/workos/workos-kotlin/issues/357))
* Upgrade to Java 17 and migrate any Fuel-based HTTP customization to `OkHttpClient`
* Recreate `WorkOS` clients with constructor-based configuration instead of mutating `apiHostname`, `https`, or `port`
* Update service accessors and method names across the SDK, including renamed services such as `PortalApi` -> `AdminPortal`, `MfaApi` -> `MultiFactorAuth`, and `FgaApi` -> `Authorization`
* Replace per-service model, type, and builder imports with the consolidated shared packages and newer flat method arguments or option classes
* Adopt the new pagination, webhook verification, and request handling patterns used across v5
* Review any existing FGA integrations closely, since the v5 Authorization API is a redesign rather than a direct rename

### Features

* Release the next major SDK version ([#357](https://github.com/workos/workos-kotlin/issues/357)) ([0b2b9e8](https://github.com/workos/workos-kotlin/commit/0b2b9e86d63ed011ea7a4888fc14e39bc9326fcb))
* Regenerate service clients from the OpenAPI spec and standardize retries, pagination, request overrides, and error handling across the SDK
* Consolidate generated models and enums into shared packages to reduce service-specific surface area
* Move webhook signature verification to the standalone `Webhook` helper while keeping webhook endpoint management on the `Webhooks` service

Please read the v5 migration guide before upgrading: https://github.com/workos/workos-kotlin/blob/v5.0.0/docs/V5_MIGRATION_GUIDE.md

## [4.25.0](https://github.com/workos/workos-kotlin/compare/v4.24.0...v4.25.0) (2026-04-30)


### Features

* Add external_id to User model ([#371](https://github.com/workos/workos-kotlin/issues/371)) ([6355ffc](https://github.com/workos/workos-kotlin/commit/6355ffc0bad787eab90ee9924699883be09405b5))

## [4.24.0](https://github.com/workos/workos-kotlin/compare/v4.23.0...v4.24.0) (2026-04-30)


### Features

* Add `user` to organization membership response ([#369](https://github.com/workos/workos-kotlin/issues/369)) ([de6e1c4](https://github.com/workos/workos-kotlin/commit/de6e1c4f19410afa207aefeec29333db746a3b04))
* Add Authorization (FGA) API endpoints ([#353](https://github.com/workos/workos-kotlin/issues/353)) ([23fdabd](https://github.com/workos/workos-kotlin/commit/23fdabdd3e0af24b69e5c759ae863df77858ebee))
* Add custom_attributes field to OrganizationMembership ([#306](https://github.com/workos/workos-kotlin/issues/306)) ([7e4643e](https://github.com/workos/workos-kotlin/commit/7e4643ebb5f0f462ae9e0c9b30206ceca0e9fc29))
* Add email field to DirectoryUser ([#321](https://github.com/workos/workos-kotlin/issues/321)) ([f6defcb](https://github.com/workos/workos-kotlin/commit/f6defcb72d498bc564ab0b103b34a62a4cb289a3))
* Add missing event models, types, and tests ([#347](https://github.com/workos/workos-kotlin/issues/347)) ([9f1ec27](https://github.com/workos/workos-kotlin/commit/9f1ec27f9f4f723a5af34de53d28c90e6311511c))
* add organization_id to OrganizationDomain model ([#350](https://github.com/workos/workos-kotlin/issues/350)) ([a261a3e](https://github.com/workos/workos-kotlin/commit/a261a3efa6773bf3bcc27ebe3234b23b8fee841f))
* add release-please for automated releases ([#322](https://github.com/workos/workos-kotlin/issues/322)) ([087273f](https://github.com/workos/workos-kotlin/commit/087273f10a4b89bd6261f2f786ae18b2a8d0d53c))
* Add resourceExternalId optional filter param for listAuthorizationResources ([#368](https://github.com/workos/workos-kotlin/issues/368)) ([2219cf6](https://github.com/workos/workos-kotlin/commit/2219cf65024422ee1c7886a3fe6f670c3d81d0fe))
* **user-management:** add directoryManaged to OrganizationMembership ([#336](https://github.com/workos/workos-kotlin/issues/336)) ([b0ad0d3](https://github.com/workos/workos-kotlin/commit/b0ad0d3f8d7579733c9eea78393317618528037c))


### Bug Fixes

* Add missing portal intents ([#345](https://github.com/workos/workos-kotlin/issues/345)) ([912e64d](https://github.com/workos/workos-kotlin/commit/912e64d25f98c49b0b3b725e7b2095fb557d9ce3))
* Add organization domain verification webhook events ([#346](https://github.com/workos/workos-kotlin/issues/346)) ([f92ac24](https://github.com/workos/workos-kotlin/commit/f92ac24dc63b5fe9501e9e58398db5dea98027ef))
* Make UserUpdated extend User for dsync.user.updated events ([#334](https://github.com/workos/workos-kotlin/issues/334)) ([7b1782f](https://github.com/workos/workos-kotlin/commit/7b1782f59b4fb0d3d5b58c7740f5c54252acfb01))
* remove @JsonCreator from all-default-param classes for Jackson 2.21.x compatibility ([#354](https://github.com/workos/workos-kotlin/issues/354)) ([118d1b3](https://github.com/workos/workos-kotlin/commit/118d1b382ef7a9e5f053f936814ec50c7374d68d))
* Remove extractVersion from matchUpdateTypes rules ([#366](https://github.com/workos/workos-kotlin/issues/366)) ([581225c](https://github.com/workos/workos-kotlin/commit/581225c304328d5529cf4a42d3ba9a51adec1c38))
* serialize list query params as comma-separated lowercase values ([#355](https://github.com/workos/workos-kotlin/issues/355)) ([f62826c](https://github.com/workos/workos-kotlin/commit/f62826cf30d0d8863c23f2f1dedc858bde7c81c5))
* update renovate rules ([#319](https://github.com/workos/workos-kotlin/issues/319)) ([740b5fa](https://github.com/workos/workos-kotlin/commit/740b5fa7a942fa39185c66cc1eafeb8a3ade5fff))
* use explicit JsonCreator.Mode.PROPERTIES for Jackson 2.21.0 compatibility ([#348](https://github.com/workos/workos-kotlin/issues/348)) ([52aed90](https://github.com/workos/workos-kotlin/commit/52aed907ee95a93ea61ba54698aaca153fc03580))

## [4.23.0](https://github.com/workos/workos-kotlin/compare/workos-v4.22.0...workos-v4.23.0) (2026-04-23)


### Features

* Add resourceExternalId optional filter param for listAuthorizationResources ([#368](https://github.com/workos/workos-kotlin/issues/368)) ([2219cf6](https://github.com/workos/workos-kotlin/commit/2219cf65024422ee1c7886a3fe6f670c3d81d0fe))


### Bug Fixes

* Remove extractVersion from matchUpdateTypes rules ([#366](https://github.com/workos/workos-kotlin/issues/366)) ([581225c](https://github.com/workos/workos-kotlin/commit/581225c304328d5529cf4a42d3ba9a51adec1c38))

## [4.22.0](https://github.com/workos/workos-kotlin/compare/workos-v4.21.0...workos-v4.22.0) (2026-04-08)


### Features

* Add Authorization (FGA) API endpoints ([#353](https://github.com/workos/workos-kotlin/issues/353)) ([23fdabd](https://github.com/workos/workos-kotlin/commit/23fdabdd3e0af24b69e5c759ae863df77858ebee))
* add organization_id to OrganizationDomain model ([#350](https://github.com/workos/workos-kotlin/issues/350)) ([a261a3e](https://github.com/workos/workos-kotlin/commit/a261a3efa6773bf3bcc27ebe3234b23b8fee841f))


### Bug Fixes

* remove @JsonCreator from all-default-param classes for Jackson 2.21.x compatibility ([#354](https://github.com/workos/workos-kotlin/issues/354)) ([118d1b3](https://github.com/workos/workos-kotlin/commit/118d1b382ef7a9e5f053f936814ec50c7374d68d))
* serialize list query params as comma-separated lowercase values ([#355](https://github.com/workos/workos-kotlin/issues/355)) ([f62826c](https://github.com/workos/workos-kotlin/commit/f62826cf30d0d8863c23f2f1dedc858bde7c81c5))

## [4.21.0](https://github.com/workos/workos-kotlin/compare/workos-v4.20.0...workos-v4.21.0) (2026-03-26)


### Features

* Add missing event models, types, and tests ([#347](https://github.com/workos/workos-kotlin/issues/347)) ([9f1ec27](https://github.com/workos/workos-kotlin/commit/9f1ec27f9f4f723a5af34de53d28c90e6311511c))


### Bug Fixes

* Add organization domain verification webhook events ([#346](https://github.com/workos/workos-kotlin/issues/346)) ([f92ac24](https://github.com/workos/workos-kotlin/commit/f92ac24dc63b5fe9501e9e58398db5dea98027ef))
* use explicit JsonCreator.Mode.PROPERTIES for Jackson 2.21.0 compatibility ([#348](https://github.com/workos/workos-kotlin/issues/348)) ([52aed90](https://github.com/workos/workos-kotlin/commit/52aed907ee95a93ea61ba54698aaca153fc03580))

## [4.20.0](https://github.com/workos/workos-kotlin/compare/workos-v4.19.1...workos-v4.20.0) (2026-03-24)


### Features

* **user-management:** add directoryManaged to OrganizationMembership ([#336](https://github.com/workos/workos-kotlin/issues/336)) ([b0ad0d3](https://github.com/workos/workos-kotlin/commit/b0ad0d3f8d7579733c9eea78393317618528037c))


### Bug Fixes

* Add missing portal intents ([#345](https://github.com/workos/workos-kotlin/issues/345)) ([912e64d](https://github.com/workos/workos-kotlin/commit/912e64d25f98c49b0b3b725e7b2095fb557d9ce3))

## [4.19.1](https://github.com/workos/workos-kotlin/compare/workos-v4.19.0...workos-v4.19.1) (2026-03-04)


### Bug Fixes

* Make UserUpdated extend User for dsync.user.updated events ([#334](https://github.com/workos/workos-kotlin/issues/334)) ([7b1782f](https://github.com/workos/workos-kotlin/commit/7b1782f59b4fb0d3d5b58c7740f5c54252acfb01))

## [4.19.0](https://github.com/workos/workos-kotlin/compare/workos-v4.18.1...workos-v4.19.0) (2026-02-26)


### Features

* Add custom_attributes field to OrganizationMembership ([#306](https://github.com/workos/workos-kotlin/issues/306)) ([7e4643e](https://github.com/workos/workos-kotlin/commit/7e4643ebb5f0f462ae9e0c9b30206ceca0e9fc29))
* Add email field to DirectoryUser ([#321](https://github.com/workos/workos-kotlin/issues/321)) ([f6defcb](https://github.com/workos/workos-kotlin/commit/f6defcb72d498bc564ab0b103b34a62a4cb289a3))
* add release-please for automated releases ([#322](https://github.com/workos/workos-kotlin/issues/322)) ([087273f](https://github.com/workos/workos-kotlin/commit/087273f10a4b89bd6261f2f786ae18b2a8d0d53c))


### Bug Fixes

* update renovate rules ([#319](https://github.com/workos/workos-kotlin/issues/319)) ([740b5fa](https://github.com/workos/workos-kotlin/commit/740b5fa7a942fa39185c66cc1eafeb8a3ade5fff))
