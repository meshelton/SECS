package io.github.meshelton.secs

import java.util.UUID
import scala.collection.mutable.{ArrayBuffer, HashMap}

/**
  * An entity
  * 
  * Represents an entity and stores some metadata for it.
  * These should only be created by a [[io.github.meshelton.secs.EntityManager EntityManager]]
  */
class Entity protected[secs](val id: String, 
                             entityManager: EntityManager,
                             tag: String = "No tag provided"){

  def add[T <: Component](component: T)
         (implicit compManager: ComponentManager[T]): Entity = {
    compManager.addComponent(this, component)
    this
  }

  def remove[T <: Component](component: T)
                     (implicit compManager: ComponentManager[T]): Entity = {
    compManager.removeComponent(this)
    this
  }

  def get[T <: Component](implicit compManager: ComponentManager[T]): Option[T] = {
    compManager.getComponent(this)
  }
  
  def getAll(): List[Component] = {
    entityManager.getComponentsOfEntity(this)
  }

}

object Entity {
  def create(implicit entityManager: EntityManager): Entity = {
    entityManager.createEntity()
  }
  def destroy(entity: Entity)(implicit entityManager: EntityManager) = {
    entityManager.destroyEntity(entity)
  }
}

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
        case Some(x) => new Entity(UUID.randomUUID().toString, this, x)
        case None => new Entity(UUID.randomUUID().toString, this)
      }
    entities += newEntity
    newEntity
  }

  /**
    * Destroys the passed in [[io.github.meshelton.secs.Entity Entity]]
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
    * Gets all the components that are associated with the passed in Entity
    * 
    * Pattern matching can be used to get the component types of this sequence
    * 
    * @param entity the Entity that the components are associated with
    * @return A sequence of the components that are associated with this entity
    */
  def getComponentsOfEntity(entity: Entity): List[Component] = {
    componentManagers.map(_(entity)).flatten.map{ (comp) => 
      (comp: @unchecked ) match {
        case Some(x: Component) => x
      }
    }.toList
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
