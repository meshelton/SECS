package io.github.meshelton.secs

import java.util.UUID
import scala.collection.mutable.{ArrayBuffer, HashMap}

/**
  * An entity
  * 
  * Represents an entity and stores some metadata for it.
  * These should only be created by a [[io.github.meshelton.secs.EntityManager EntityManager]]
  * 
  * @constructor create a new [[io.github.meshelton.secs.Entity]] with a UUID and usertag
  * @param id the entities UUID
  * @param tag the userdata, optional with a defualt value of "No tag provided"
  */
case class Entity protected[secs](val id: UUID, val tag: String = "No tag provided")

/**
  * A managers of entities
  * 
  * Manages the creation and destructions of [[io.github.meshelton.secs.EntityManager EntityManager]].
  * Defines a overall entity system with registered [[io.github.meshelton.secs.ComponentManager ComponentManagers]] and [[io.github.meshelton.secs.System Systems]]
  * Entities should only be created by an instance of this class.
  */
class EntityManager {

  private val entities = ArrayBuffer[Entity]()
  private val componentManagers = ArrayBuffer[ComponentManager[_]]()
  private val systems = ArrayBuffer[System]()

  /**
    * Creates a new Entity with no tag
    * 
    * @return the new entity
    */
  def apply(): Entity = createEntity()

  /**
    * Creates a new Entity with supplied tag
    * 
    * @param tag the userdata
    * @return the new Entity
    */
  def apply(tag: String) = createEntity(tag)

  /**
    * Destroys the passed in [[io.github.meshelton.secs.Entity]]
    * 
    * Removes the passed in Entity from this and all registered ComponentManagers
    * 
    * @param entity the Entity to destroy
    */
  def apply(entity: Entity) = destroyEntity(entity)

  /**
    * Creates a new Entity with supplied tag
    * 
    * @param tag the userdata
    * @return the new Entity
    */
  def createEntity(tag: String): Entity = createEntity(Some(tag))

  /**
    * Creates a new Entity with no tag
    * 
    * @return the new Entity
    */
  def createEntity(): Entity = createEntity(None)

  private def createEntity(tag: Option[String]): Entity = {
    val newEntity = tag match {
        case Some(x) => new Entity(UUID.randomUUID(), x)
        case None => new Entity(UUID.randomUUID())
      }
    entities += newEntity
    newEntity
  }

  /**
    * Destroys the passed in [[io.github.meshelton.secs.Entity]]
    * 
    * Removes the passed in Entity from this and all registered ComponentManagers
    * 
    * @param entity the [[io.github.meshelton.secs.Entity]] to destroy
    */
  def destroyEntity(entity: Entity) = {
    entities -= entity
    componentManagers.foreach( _.removeComponent(entity) )
  }

  /**
    * Registers a ComponentManager with this 
    * 
    * @param componentManager the ComponentManager to be registered
    */
  def registerComponentManager(componentManager: ComponentManager[_]) = {
    componentManagers += componentManager
  }

  /**
    * Deregisters a ComponentManager with this 
    * 
    * @param componentManager the ComponentManager to be deregistered
    */
  def deregisterComponentManager(componentManager: ComponentManager[_]) = {
    componentManagers -= componentManager
  }

  /**
    * Registers a System with this 
    * 
    * @param system the System to be registered
    */
  def registerSystem(system: System) = {
    systems += system
  }

  /**
    * Deregisters a [[io.github.meshelton.secs.System]] with this
    * 
    * @param system the System to be deregistered
    */
  def deregisterSystem(system: System) = {
    systems -= system
  }

  /**
    * Updates all Systems that are set to update and registered with this EntityManager
    * 
    * Be careful of double updating!
    * Either use this method to update all systems simultaneously or explicitly update systems
    * By default new systems are registered and set to NOT update
    * 
    * @param delta the time between frames
    */
  def update(delta: Float) = {
    // TODO Because the order of updates is important should provide some methods to change the
    // stored order of system
    systems.filter(_.updating).foreach(_.update(delta))
  }

}
