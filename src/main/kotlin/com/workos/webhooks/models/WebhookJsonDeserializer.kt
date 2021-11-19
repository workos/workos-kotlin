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
 * Custom JSON deserializer for webhook events.
 */
class WebhookJsonDeserializer : JsonDeserializer<Webhook>() {
  private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
  override fun deserialize(jp: JsonParser?, ctxt: DeserializationContext?): Webhook {
    val rootNode = jp?.codec?.readTree<TreeNode>(jp)
    val id = mapper.readValue(rootNode?.get("id")?.traverse(), String::class.java)
    val eventType = mapper.readValue(rootNode?.get("event")?.traverse(), EventType::class.java)!!
    val data = rootNode?.get("data")

    return when (eventType) {
      EventType.ConnectionActivated, EventType.ConnectionDeactivated, EventType.ConnectionDeleted
      -> Webhook(id, eventType, mapper.readValue(data?.traverse(), Connection::class.java))
      EventType.DirectoryActivated, EventType.DirectoryDeactivated, EventType.DirectoryDeleted,
      -> Webhook(id, eventType, mapper.readValue(data?.traverse(), Directory::class.java))
      EventType.DirectoryUserCreated, EventType.DirectoryUserUpdated, EventType.DirectoryUserDeleted,
      -> Webhook(id, eventType, mapper.readValue(data?.traverse(), User::class.java))
      EventType.DirectoryGroupCreated, EventType.DirectoryGroupUpdated, EventType.DirectoryGroupDeleted,
      -> Webhook(id, eventType, mapper.readValue(data?.traverse(), Group::class.java))
      EventType.DirectoryGroupUserAdded, EventType.DirectoryGroupUserRemoved,
      -> Webhook(id, eventType, mapper.readValue(data?.traverse(), DirectoryGroupUserChange::class.java))
    }
  }
}
