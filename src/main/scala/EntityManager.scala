package com.shelton

import java.util.UUID
import scala.collection.mutable.{ArrayBuffer, HashMap}

class EntityManager {

  case class Entity protected[EntityManager](val id: UUID)

  val entities = ArrayBuffer[Entity]()
  val componentManagers = ArrayBuffer[ComponentManager]()

  def createEntity(): Entity = {
    val newEntity = new Entity(UUID.randomUUID())
    entities += newEntity
    newEntity
  }

  def destroyEntity(entity: Entity) = {
    entities -= entity
    componentManagers.foreach( 
      (cm) => {
        cm.removeEntity(entity.asInstanceOf[cm.entityManager.Entity])
      }
      
    )
  }

  def registerComponentManager(cm: ComponentManager) = {
    componentManagers += cm
  }

}
