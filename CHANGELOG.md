# Changelog

## [6.5.0](https://github.com/workos/workos-kotlin/compare/v6.4.0...v6.5.0) (2026-07-06)

* [#408](https://github.com/workos/workos-kotlin/pull/408) fix(generated): regenerate from spec

  **Features**
  * **[user_management](https://workos.com/docs/reference/authkit/user)**:
    * Added model `UserRoleAssignmentSource`
    * Added `source` to `UserRoleAssignment`
    * Added enum `UserRoleAssignmentSourceType`
    * Added parameter `UserManagementAuthentication.authorize.max_age`
    * Added endpoint `GET /user_management/cors_origins`
    * Added endpoint `GET /user_management/redirect_uris`

  **Fixes**
  * Restore mistakenly removed CreateMagicAuth logic from previous release

## [6.4.0](https://github.com/workos/workos-kotlin/compare/v6.3.0...v6.4.0) (2026-07-02)

* [#402](https://github.com/workos/workos-kotlin/pull/402) fix(generated): regenerate from spec

  **Features**
  * **[pipes](https://workos.com/docs/reference/pipes)**:
    * Added model `DataIntegrationCredentialsResponse`
    * Added model `DataIntegrationCredentialsResponseCredential`
    * Added model `DataIntegrationsUpsertApiKeyRequest`
    * Added model `DataIntegrationsVendCredentialsRequest`
    * Added enum `DataIntegrationCredentialsResponseError`
    * Added endpoint `PUT /data-integrations/{slug}/api-key`
    * Added endpoint `POST /data-integrations/{slug}/credentials`

* [#404](https://github.com/workos/workos-kotlin/pull/404) fix(generated): regenerate from spec

  **⚠️ Breaking**
  * **[user_management](https://workos.com/docs/reference/authkit/user)**:
    * Removed model `SessionReauthenticated`
    * Removed model `SessionReauthenticatedData`
    * Removed model `SessionReauthenticatedDataImpersonator`
    * Removed enum `SessionReauthenticatedDataAuthMethod`
    * Removed enum `SessionReauthenticatedDataStatus`

  **Features**
  * **[webhooks](https://workos.com/docs/reference/webhooks)**:
    * Added `agent.registration.created` to `CreateWebhookEndpointEvents`
    * Added `agent.registration.claim.attempt.created` to `CreateWebhookEndpointEvents`
    * Added `agent.registration.claim.completed` to `CreateWebhookEndpointEvents`
    * Added `agent.registration.credential.issued` to `CreateWebhookEndpointEvents`
    * Added `agent.registration.organization.switched` to `CreateWebhookEndpointEvents`
    * Added `authentication.reauthentication_succeeded` to `CreateWebhookEndpointEvents`
    * Added `agent.registration.created` to `UpdateWebhookEndpointEvents`
    * Added `agent.registration.claim.attempt.created` to `UpdateWebhookEndpointEvents`
    * Added `agent.registration.claim.completed` to `UpdateWebhookEndpointEvents`
    * Added `agent.registration.credential.issued` to `UpdateWebhookEndpointEvents`
    * Added `agent.registration.organization.switched` to `UpdateWebhookEndpointEvents`
    * Added `authentication.reauthentication_succeeded` to `UpdateWebhookEndpointEvents`
  * **[webhooks](https://workos.com/docs/reference/webhooks)**:
    * Added `session.reauthenticated` to `CreateWebhookEndpointEvents`
    * Added `session.reauthenticated` to `UpdateWebhookEndpointEvents`
  * **[webhooks](https://workos.com/docs/reference/webhooks)**:
    * Added `pipes.connected_account.connection_failed` to `CreateWebhookEndpointEvents`
    * Added `pipes.connected_account.connection_failed` to `UpdateWebhookEndpointEvents`
  * **[user_management](https://workos.com/docs/reference/authkit/user)**:
    * Added model `UserRoleAssignmentSource`
    * Added `source` to `UserRoleAssignment`
    * Added enum `UserRoleAssignmentSourceType`
    * Added parameter `UserManagementAuthentication.authorize.max_age`
    * Added endpoint `GET /user_management/cors_origins`
    * Added endpoint `GET /user_management/redirect_uris`
  * **[audit_logs](https://workos.com/docs/reference/audit-logs)**:
    * Changed the format of `AuditLogExportCreation.range_start`
    * Changed the format of `AuditLogExportCreation.range_end`
  * **[audit_logs](https://workos.com/docs/reference/audit-logs)**:
    * Added `expired` to `AuditLogExportState`

  **Fixes**
  * **[admin_portal](https://workos.com/docs/reference/admin-portal)**:
    * Removed `intent_options` from `GenerateLink`
  * **[webhooks](https://workos.com/docs/reference/webhooks)**:
    * Removed `session.reauthenticated` from `CreateWebhookEndpointEvents`
    * Removed `session.reauthenticated` from `UpdateWebhookEndpointEvents`

* [#406](https://github.com/workos/workos-kotlin/pull/406) feat(generated): regenerate from spec (1 change)

  **Features**
  * **[pipes](https://workos.com/docs/reference/pipes)**:
    * Added model `DataIntegrationCredentialsDto`
    * Added model `CustomProviderDefinition`
    * Added model `CreateDataIntegration`
    * Added model `UpdateCustomProviderDefinition`
    * Added model `UpdateDataIntegration`
    * Added model `DataIntegration`
    * Added model `DataIntegrationList`
    * Added model `DataIntegrationListListMetadata`
    * Added model `DataIntegrationCredential`
    * Added model `DataIntegrationCustomProvider`
    * Added enum `DataIntegrationCredentialsType`
    * Added enum `CustomProviderDefinitionAuthenticateVia`
    * Added enum `UpdateCustomProviderDefinitionAuthenticateVia`
    * Added enum `DataIntegrationState`
    * Added enum `DataIntegrationCredentialType`
    * Added enum `DataIntegrationCustomProviderAuthenticateVia`
    * Added endpoint `GET /data-integrations`
    * Added endpoint `POST /data-integrations`
    * Added endpoint `GET /data-integrations/{slug}`
    * Added endpoint `PUT /data-integrations/{slug}`
    * Added endpoint `DELETE /data-integrations/{slug}`
    * Added endpoint `POST /user_management/users/{user_id}/connected_accounts/{slug}`
    * Added endpoint `PUT /user_management/users/{user_id}/connected_accounts/{slug}`

* [#407](https://github.com/workos/workos-kotlin/pull/407) feat(generated): regenerate from spec (2 changes)

  **Features**
  * **[user_management](https://workos.com/docs/reference/authkit/user)**:
    * Added model `SendRadarSmsChallenge`
    * Added model `SendRadarSmsChallengeResponse`
    * Added model `UrnWorkosOAuthGrantTypeRadarEmailChallengeCodeSessionAuthenticateRequest`
    * Added model `UrnWorkosOAuthGrantTypeRadarSmsChallengeCodeSessionAuthenticateRequest`
    * Added model `MagicAuthSendMagicAuthCodeAndReturnResponse`
    * Added model `UserCreateResponse`
    * Added `ip_address` to `CreateMagicCodeAndReturn`
    * Added `user_agent` to `CreateMagicCodeAndReturn`
    * Added `radar_auth_attempt_id` to `CreateMagicCodeAndReturn`
    * Added `signals_id` to `CreateMagicCodeAndReturn`
    * Added `ip_address` to `CreateUser`
    * Added `user_agent` to `CreateUser`
    * Added `signals_id` to `CreateUser`
    * Added `signals_id` to `AuthorizationCodeSessionAuthenticateRequest`
    * Added `signals_id` to `PasswordSessionAuthenticateRequest`
    * Added `radar_auth_attempt_id` to `PasswordSessionAuthenticateRequest`
    * Added `radar_auth_attempt_id` to `UrnWorkosOAuthGrantTypeMagicAuthCodeSessionAuthenticateRequest`
    * Added endpoint `POST /user_management/radar_challenges`
  * **[radar](https://workos.com/docs/reference/radar)**:
    * Added `signals_id` to `RadarStandaloneAssessRequest`

  **Fixes**
  * **[user_management](https://workos.com/docs/reference/authkit/user)**:
    * Changed request body for `UserManagementAuthentication.authenticate`
    * Changed response of `UserManagementUsers.create` from `User` to `UserCreateResponse`
    * Changed response of `UserManagementMagicAuth.sendMagicAuthCodeAndReturn` from `MagicAuth` to `MagicAuthSendMagicAuthCodeAndReturnResponse`

## [6.3.0](https://github.com/workos/workos-kotlin/compare/v6.2.0...v6.3.0) (2026-06-30)

* [#398](https://github.com/workos/workos-kotlin/pull/398) fix(generated): regenerate from spec

  **Fixes**
  * **[organization_membership](https://workos.com/docs/reference/authkit/organization-membership)**:
    * Added `roles` to organization membership models

## [6.2.0](https://github.com/workos/workos-kotlin/compare/v6.1.0...v6.2.0) (2026-06-18)

- [#395](https://github.com/workos/workos-kotlin/pull/395) feat(generated)!: regenerate from spec (11 changes)

  **⚠️ Breaking**
  - **[organization_membership](https://workos.com/docs/reference/authkit/organization-membership)**:
    - Changed response of `UserManagementOrganizationMembership.list` from `UserOrganizationMembership` to `UserOrganizationMembershipList`
  - **[pipes](https://workos.com/docs/reference/pipes)**:
    - SDK surface change: `Pipes.createDataIntegrationToken` was renamed to `Pipes.getAccessToken`
  - **[user_management](https://workos.com/docs/reference/authkit/user)**:
    - Changed response of `UserManagementInvitations.list` from `UserInvite` to `UserInviteList`

  **Features**
  - **[authorization](https://workos.com/docs/reference/fga)**:
    - Added model `ReplaceGroupRoleAssignmentEntry`
    - Added model `ReplaceGroupRoleAssignments`
    - Added model `DeleteGroupRoleAssignmentsByCriteria`
    - Added endpoint `POST /authorization/groups/{group_id}/role_assignments`
    - Added endpoint `PUT /authorization/groups/{group_id}/role_assignments`
    - Added endpoint `DELETE /authorization/groups/{group_id}/role_assignments`
    - Added endpoint `GET /authorization/groups/{group_id}/role_assignments/{role_assignment_id}`
    - Added endpoint `DELETE /authorization/groups/{group_id}/role_assignments/{role_assignment_id}`
  - **[client](https://workos.com/docs/reference)**:
    - Added model `ClientApiToken`
    - Added model `ClientApiTokenResponse`
    - Added service `Client`
  - **[connect](https://workos.com/docs/reference/workos-connect/standalone)**:
    - Added `auth_method` to `ConnectedAccount`
    - Added `api_key_last_4` to `ConnectedAccount`
    - Added enum `ConnectedAccountAuthMethod`
  - **[groups](https://workos.com/docs/reference/groups)**:
    - Added model `CreateGroupRoleAssignment`
    - Added model `GroupRoleAssignment`
    - Added model `GroupRoleAssignmentList`
    - Added model `GroupRoleAssignmentResource`
  - **[organization_membership](https://workos.com/docs/reference/authkit/organization-membership)**:
    - Added model `UserOrganizationMembershipList`
    - Added model `UserOrganizationMembershipListListMetadata`
  - **[pipes](https://workos.com/docs/reference/pipes)**:
    - Added model `DataIntegrationCredentials`
    - Added model `DataIntegrationConfigurationResponse`
    - Added model `DataIntegrationConfigurationListResponse`
    - Added model `ConfigureDataIntegrationBody`
    - Added `auth_methods` to `DataIntegrationsListResponseData`
    - Added `auth_method` to `DataIntegrationsListResponseDataConnectedAccount`
    - Added `api_key_last_4` to `DataIntegrationsListResponseDataConnectedAccount`
    - Added enum `DataIntegrationCredentialsCredentialsType`
    - Added enum `DataIntegrationsListResponseDataAuthMethods`
    - Added enum `DataIntegrationsListResponseDataConnectedAccountAuthMethod`
    - Added service `PipesProvider`
  - **[user_management](https://workos.com/docs/reference/authkit/user)**:
    - Added model `UserInviteList`
    - Added model `UserInviteListListMetadata`
    - Made `AuthorizationCodeSessionAuthenticateRequest.client_secret` optional
    - Made `RefreshTokenSessionAuthenticateRequest.client_secret` optional
  - **[widgets](https://workos.com/docs/reference/widgets)**:
    - Added `widgets:pipes:manage` to `WidgetSessionTokenScopes`

## [6.1.0](https://github.com/workos/workos-kotlin/compare/v6.0.0...v6.1.0) (2026-06-17)

### Bug Fixes

* **deps:** update minor and patch updates ([#393](https://github.com/workos/workos-kotlin/issues/393)) ([9c224b9](https://github.com/workos/workos-kotlin/commit/9c224b932547d3aeb080294982ab0bbb98abab54))
* **renovate:** explicitly enable minor and patch updates ([#389](https://github.com/workos/workos-kotlin/issues/389)) ([bce61a6](https://github.com/workos/workos-kotlin/commit/bce61a67f13472a40c2a657614d92342d95dc130))

- [#391](https://github.com/workos/workos-kotlin/pull/391) feat(generated): regenerate from spec (9 changes)

  **Features**
  - **[api_keys](https://workos.com/docs/reference/authkit/api-keys)**:
    - Added model `ExpireApiKey`
    - Added model `ApiKeyUpdated`
    - Added model `ApiKeyUpdatedData`
    - Added model `ApiKeyUpdatedDataOwner`
    - Added model `UserApiKeyUpdatedDataOwner`
    - Added model `ApiKeyUpdatedDataPreviousAttribute`
    - Added endpoint `POST /api_keys/{id}/expire`
  - **[audit_logs](https://workos.com/docs/reference/audit-logs)**:
    - Added `Snowflake` to `AuditLogConfigurationLogStreamType`
  - **[connect](https://workos.com/docs/reference/workos-connect/standalone)**:
    - Added `name` to `UserObject`
  - **[directory_sync](https://workos.com/docs/reference/directory-sync)**:
    - Added model `DsyncTokenCreated`
    - Added model `DsyncTokenCreatedData`
    - Added model `DsyncTokenRevoked`
    - Added model `DsyncTokenRevokedData`
  - **[user_management](https://workos.com/docs/reference/authkit/user)**:
    - Added `name` to user management models
  - **[webhooks](https://workos.com/docs/reference/webhooks)**:
    - Added `api_key.updated` to `CreateWebhookEndpointEvents`
    - Added `api_key.updated` to `UpdateWebhookEndpointEvents`

## [6.0.0](https://github.com/workos/workos-kotlin/compare/v5.1.0...v6.0.0) (2026-05-26)

### Bug Fixes

* **deps:** update dependency com.nimbusds:nimbus-jose-jwt to v10 ([#384](https://github.com/workos/workos-kotlin/issues/384)) ([b866a33](https://github.com/workos/workos-kotlin/commit/b866a33c790fdaf91610435dbcd2db588ba07463))
* **deps:** update dependency com.squareup.okhttp3:okhttp to v5 ([#385](https://github.com/workos/workos-kotlin/issues/385)) ([8d20796](https://github.com/workos/workos-kotlin/commit/8d20796f671c33862fc1987eb9ee7f2af490b34a))
* **deps:** update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.11.0 ([#381](https://github.com/workos/workos-kotlin/issues/381)) ([09130f0](https://github.com/workos/workos-kotlin/commit/09130f004d01b26eb0ef78cc33ac13c4c89439ae))

* [#388](https://github.com/workos/workos-kotlin/pull/388) feat(generated)!: regenerate from spec (13 changes)

  **⚠️ Breaking**
  * **vault:** Rewrite Vault service from auto-generated spec
    * Vault was previously a hand-maintained class; it is now auto-generated and accessible via `WorkOS.vault`
    * Renamed methods: `listObjects` → `listKv`, `createObject` → `createKv`, `readObject`/`readObjectByName` → `getKv`/`getName`, `getObjectMetadata` → `listKvMetadata`, `updateObject` → `updateKv`, `deleteObject` → `deleteKv`, `listObjectVersions` → `listKvVersions`, `decryptDataKey` → `createDecrypt`
    * Added new method: `createRekey`
    * Removed types previously defined in `Vault.kt`: `DataKey`, `DataKeyPair`, `VaultObject`, `ObjectDigest`, `ObjectUpdateBy`, `ObjectMetadata`, `ObjectVersion`, and `KeyContext` type alias — replaced by generated models in `com.workos.models.*`
    * Client-side `encrypt` and `decrypt` helper methods are preserved
  * **user_management:** Move organization membership methods to separate service
    * Removed organization membership methods from `UserManagement`: `listOrganizationMemberships`, `createOrganizationMembership`, `getOrganizationMembership`, `updateOrganizationMembership`, `deleteOrganizationMembership`, `deactivateOrganizationMembership`, `reactivateOrganizationMembership`
    * These methods are now available on the new `OrganizationMembershipService` as `list`, `create`, `get`, `update`, `delete`, `deactivate`, `reactivate`
    * Removed `CreateUserRole` class from `UserManagement` (now in `OrganizationMembershipService`)
    * Deleted `UserManagementOrganizationMembershipGroups` class and its package
    * Updated `WorkOS.userManagementOrganizationMembershipGroups` to `WorkOS.organizationMembership`
  * **authorization:** Remove search parameter from listResources operations
    * Removed `search` parameter from `listResources` method and related variants
    * Removed `search` parameter from `listResourcesById`, `listResourcesByExternalId`, and their suspend variants
    * Added new filter parameters to `listRoleAssignments`: `resourceId`, `resourceExternalId`, `resourceTypeSlug`
    * Added new filter parameter to `listRoleAssignmentsForResourceByExternalId`: `roleSlug`
    * Added new filter parameter to `listRoleAssignmentsForResource`: `roleSlug`
  * **radar:** Remove device fingerprint and bot score parameters from createAttempt
    * Removed `deviceFingerprint` parameter from `Radar.createAttempt` and `createAttemptSuspend`
    * Removed `botScore` parameter from `Radar.createAttempt` and `createAttemptSuspend`
    * Updated `RadarStandaloneAssessRequest` model to remove `device_fingerprint` and `bot_score` fields
  * **radar:** Update RadarStandaloneAssessRequestAction enum values
    * Removed enum values: `Login`, `Signup`, `SignUp2`, `SignIn2`, `SignIn3`, `SignUp3`
    * Changed `SignUp` value from 'sign up' to 'sign-up'
    * Changed `SignIn` value from 'sign in' to 'sign-in'
  * **radar:** Remove enum values from RadarStandaloneResponseControl
    * Removed `CredentialStuffing` enum value
    * Removed `IpSignUpRateLimit` enum value
  * **audit_logs:** Rename AuditLog model classes and update references
    * Renamed `AuditLogActionJson` to `AuditLogAction`
    * Renamed `AuditLogExportJson` to `AuditLogExport`
    * Renamed `AuditLogsRetentionJson` to `AuditLogsRetention`
    * Deleted `AuditLogSchemaJson`; its fields (`version`, `createdAt`, `objectType`) were absorbed into `AuditLogSchema`
    * Renamed `AuditLogSchemaJsonActor` to `AuditLogSchemaActorInput`
    * Renamed `AuditLogSchemaJsonTarget` to `AuditLogSchemaTargetInput`
    * Added new `AuditLogSchemaInput` model for create/update operations
    * Changed `AuditLogs.createSchema` parameter types from `AuditLogSchemaTarget`/`AuditLogSchemaActor` to `AuditLogSchemaTargetInput`/`AuditLogSchemaActorInput`
    * Updated all method return types and parameter types in `AuditLogs` service
  * **webhooks:** Rename WebhookEndpoint model and status enum
    * Renamed `WebhookEndpointJson` to `WebhookEndpoint`
    * Renamed `WebhookEndpointJsonStatus` to `WebhookEndpointStatus`
    * Updated `UpdateWebhookEndpointStatus` to alias the renamed status enum
    * Updated all Webhooks service method signatures and return types
    * Updated parameter type for `status` in `updateEndpoint` method
  * **generated:** Rename model classes and type aliases for consistency
    * Changed `ApplicationCredentialsListItem.last_used_at` field type from `String?` to `OffsetDateTime?`
    * Changed `NewConnectApplicationSecret.last_used_at` field type from `String?` to `OffsetDateTime?`
    * Updated `FlagRuleUpdatedContextConfiguredTargetOrganization` to be an alias for `Actor`
    * Updated `FlagRuleUpdatedContextPreviousAttributeContextConfiguredTargetOrganization` to be an alias for `Actor`
    * Changed `UserManagementAuthenticationScreenHint` to be an alias for `RadarStandaloneAssessRequestAction`

  **Features**
  * **organization_membership:** Add new OrganizationMembershipService with membership and group operations
    * New service `OrganizationMembershipService` providing organization membership management
    * Includes `list`, `create`, `get`, `update`, `delete`, `deactivate`, `reactivate` operations for memberships
    * Includes `listGroups` operation to list groups for a membership
    * All operations available in blocking and suspend variants
  * **vault:** Add `WorkOS.vault` accessor and new operations
    * Vault is now accessible via `WorkOS.vault` (previously not exposed on the `WorkOS` class)
    * Key operations: `createDataKey`, `createDecrypt`, `createRekey`
    * Object operations: `listKv`, `createKv`, `getName`, `getKv`, `updateKv`, `deleteKv`, `listKvMetadata`, `listKvVersions`
    * All operations available in blocking and suspend variants
  * **api_keys:** Add expires_at field to API key models and creation methods
    * Added `expires_at` field to `ApiKey`, `OrganizationApiKey`, `OrganizationApiKeyWithValue`, `UserApiKey`, `UserApiKeyWithValue` models
    * Added `expires_at` optional parameter to `createOrganizationApiKey` method in `ApiKeys` service
    * Added `expires_at` optional parameter to `createApiKey` method in `UserManagement` service
    * Added `expires_at` field to request models: `CreateOrganizationApiKey`, `CreateUserApiKey`
    * Added `expires_at` field to event data models: `ApiKeyCreatedData`, `ApiKeyRevokedData`
  * **webhooks:** Add pipe connected account webhook event types
    * Added three new webhook event types: `PIPES_CONNECTED_ACCOUNT_CONNECTED`, `PIPES_CONNECTED_ACCOUNT_DISCONNECTED`, `PIPES_CONNECTED_ACCOUNT_REAUTHORIZATION_NEEDED`
    * Added to both `CreateWebhookEndpointEvents` and `UpdateWebhookEndpointEvents` enums
    * Added corresponding event models: `PipesConnectedAccountConnected`, `PipesConnectedAccountDisconnected`, `PipesConnectedAccountReauthorizationNeeded`
  * **generated:** Add new models and enums
    * New Vault-related models: `CreateDataKeyRequest`, `CreateDataKeyResponse`, `CreateObjectRequest`, `DecryptRequest`, `DecryptResponse`, `DeleteObjectResponse`, `RekeyRequest`, `UpdateObjectRequest`
    * New Vault-related models: `Object`, `ObjectMetadata`, `ObjectSummary`, `ObjectVersion`, `ObjectWithoutValue`, `VersionListResponse`, `ListMetadata`
    * New general models: `Actor`, `ErrorResponse`
    * New Connect models: `ConnectApplicationM2M`, `ConnectApplicationOAuth`, `ConnectApplicationOAuthRedirectUris`
    * New enum: `PipeConnectedAccountState`
    * New enum: `SortOrder`
    * New event models: `PipeConnectedAccount`, `PipesConnectedAccountConnected`, `PipesConnectedAccountDisconnected`, `PipesConnectedAccountReauthorizationNeeded`
    * Updated `WorkOSEvent` to include new Pipes webhook event types

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
