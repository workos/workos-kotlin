package com.workos.events.models

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.workos.sso.models.Connection
import com.workos.usermanagement.models.OrganizationMembership
import com.workos.usermanagement.models.User as UmUser

class EventsJsonDeserializer : JsonDeserializer<Event>() {
  private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

  init {
    mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
  }

  override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): Event {
    val rootNode = jp?.codec?.readTree<TreeNode>(jp)
    val id = mapper.readValue(rootNode?.get("id")?.traverse(), String::class.java)
    val eventType = mapper.readValue(rootNode?.get("event")?.traverse(), String::class.java)
    val createdAt = mapper.readValue(rootNode?.get("created_at")?.traverse(), String::class.java)
    val contextNode = rootNode?.get("context")
    val context = if (contextNode != null) {
      @Suppress("UNCHECKED_CAST")
      mapper.readValue(contextNode.traverse(), Map::class.java) as Map<String, Any>?
    } else null
    val data = rootNode?.get("data")

    return when {
      eventType.startsWith("authentication.") -> AuthenticationEvent(id, eventType, createdAt, context, deserializeData(data, com.workos.usermanagement.models.AuthenticationEventData::class.java))
      eventType.startsWith("email_verification.") -> EmailVerificationEvent(id, eventType, createdAt, context, deserializeData(data, com.workos.usermanagement.models.EmailVerificationEventData::class.java))
      eventType.startsWith("magic_auth.") -> MagicAuthEvent(id, eventType, createdAt, context, deserializeData(data, com.workos.usermanagement.models.MagicAuthEventData::class.java))
      eventType.startsWith("invitation.") -> InvitationEvent(id, eventType, createdAt, context, deserializeData(data, com.workos.usermanagement.models.InvitationEventData::class.java))
      eventType.startsWith("password_reset.") -> PasswordResetEvent(id, eventType, createdAt, context, deserializeData(data, com.workos.usermanagement.models.PasswordResetEventData::class.java))
      eventType.startsWith("organization.") -> OrganizationEvent(id, eventType, createdAt, context, deserializeData(data, OrganizationEventData::class.java))
      eventType.startsWith("connection.") -> ConnectionEvent(id, eventType, createdAt, context, deserializeData(data, Connection::class.java))
      eventType.startsWith("dsync.user.") -> DirectoryUserEvent(id, eventType, createdAt, context, deserializeData(data, DirectoryUserMinimal::class.java))
      eventType.startsWith("dsync.group.user_") -> DirectoryGroupMembershipEvent(id, eventType, createdAt, context, deserializeData(data, DirectoryGroupMembershipData::class.java))
      eventType.startsWith("dsync.group.") -> DirectoryGroupEvent(id, eventType, createdAt, context, deserializeData(data, DirectoryGroupEventData::class.java))
      eventType.startsWith("dsync.") -> DirectoryEvent(id, eventType, createdAt, context, deserializeData(data, DirectoryEventData::class.java))
      eventType.startsWith("user.") -> UserEvent(id, eventType, createdAt, context, deserializeData(data, UmUser::class.java))
      eventType.startsWith("organization_membership.") -> OrganizationMembershipEvent(id, eventType, createdAt, context, deserializeData(data, OrganizationMembership::class.java))
      eventType.startsWith("organization_domain.") -> OrganizationDomainEvent(id, eventType, createdAt, context, deserializeData(data, com.workos.organizations.models.OrganizationDomain::class.java))
      eventType.startsWith("role.") -> RoleEvent(id, eventType, createdAt, context, deserializeData(data, com.workos.roles.models.Role::class.java))
      eventType.startsWith("session.") -> SessionEvent(id, eventType, createdAt, context, deserializeData(data, SessionEventData::class.java))
      else -> UnknownEvent(id, eventType, createdAt, context, mapper.treeToValue(data, Any::class.java))
    }
  }

  private fun <T> deserializeData(data: TreeNode?, clazz: Class<T>): T {
    return mapper.treeToValue(data, clazz)
  }
}
