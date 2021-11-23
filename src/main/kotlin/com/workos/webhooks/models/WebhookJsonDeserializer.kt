package com.workos.webhooks.models

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.workos.directorysync.models.Directory
import com.workos.directorysync.models.Group
import com.workos.directorysync.models.User
import com.workos.sso.models.Connection

/**
 * Custom JSON deserializer for [com.workos.webhooks.models.WebhookEvent] events.
 */
class WebhookJsonDeserializer : JsonDeserializer<WebhookEvent>() {
  private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

  /**
   * @suppress
   */
  override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): WebhookEvent {
    val rootNode = jp?.codec?.readTree<TreeNode>(jp)
    val id = mapper.readValue(rootNode?.get("id")?.traverse(), String::class.java)
    val eventType = mapper.readValue(rootNode?.get("event")?.traverse(), EventType::class.java)!!
    val data = rootNode?.get("data")

    return when (eventType) {
      EventType.ConnectionActivated -> ConnectionActivatedEvent(id, eventType, deserializeData(data, Connection::class.java))
      EventType.ConnectionDeactivated -> ConnectionDeactivatedEvent(id, eventType, deserializeData(data, Connection::class.java))
      EventType.ConnectionDeleted -> ConnectionDeletedEvent(id, eventType, deserializeData(data, Connection::class.java))
      EventType.DirectoryActivated -> DirectoryActivatedEvent(id, eventType, deserializeData(data, Directory::class.java))
      EventType.DirectoryDeactivated -> DirectoryDeactivatedEvent(id, eventType, deserializeData(data, Directory::class.java))
      EventType.DirectoryDeleted -> DirectoryDeletedEvent(id, eventType, deserializeData(data, Directory::class.java))
      EventType.DirectoryUserCreated -> DirectoryUserCreatedEvent(id, eventType, deserializeData(data, User::class.java))
      EventType.DirectoryUserUpdated -> DirectoryUserUpdatedEvent(id, eventType, deserializeData(data, UserUpdated::class.java))
      EventType.DirectoryUserDeleted -> DirectoryUserDeletedEvent(id, eventType, deserializeData(data, User::class.java))
      EventType.DirectoryGroupCreated -> DirectoryGroupCreatedEvent(id, eventType, deserializeData(data, Group::class.java))
      EventType.DirectoryGroupUpdated -> DirectoryGroupUpdatedEvent(id, eventType, deserializeData(data, GroupUpdated::class.java))
      EventType.DirectoryGroupDeleted -> DirectoryGroupDeletedEvent(id, eventType, deserializeData(data, Group::class.java))
      EventType.DirectoryGroupUserAdded -> DirectoryGroupUserAddedEvent(id, eventType, deserializeData(data, DirectoryGroupUserEvent::class.java))
      EventType.DirectoryGroupUserRemoved -> DirectoryGroupUserRemovedEvent(id, eventType, deserializeData(data, DirectoryGroupUserEvent::class.java))
    }
  }

  private fun <T> deserializeData(data: TreeNode?, clazz: Class<T>): T {
    return mapper.treeToValue(data, clazz)
  }
}
