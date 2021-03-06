package io.github.meshelton.secs

import java.util.UUID
import scala.collection.mutable.{ArrayBuffer, HashMap}

/**
  * A Component
  * 
  * Components should only store mutable data and only have getter and setter methods
  */
trait Component 

/**
  * Manages a type of componenet
  * 
  * Manages the components in the system of [[io.github.meshelton.secs.ComponentManager.ComponentType ComponentType]]
  * Should be used when attempting to retrieve entities that have a specific component
  * Ideally should not be subclassed 
  * 
  * @constructor Creates a new ComponentManager registered to a EntityManager
  * @param entityManager The [[io.github.meshelton.secs.EntityManager EntityManager]] that this component manager will be registered to. It is implicitly passed in so you have to make your entity manager availble to it
  */
class ComponentManager[ComponentType <: Component](implicit val entityManager: EntityManager){

  entityManager.registerComponentManager(this)

  private val components = HashMap[Entity, ComponentType]()

  /**
    * Gets all the entities that have components managed by this ComponentManager
    * 
    * @return all the entities that have components managed by this ComponentManager
    */
  def apply() = getEntities()

  /**
    * Gets the component that is attached to entity
    * 
    * @param entity the entity that may or may not have an attached component
    * @return an option containing the component attached to the entity
    */
  def apply(entity: Entity) = getComponent(entity)

  /**
    * Adds a component to the entity overriding the old one if present
    * 
    * 
    * @param entity the entity that the new component will be attached to
    * @return the newly created component
    */
  def addComponent(entity: Entity, component: ComponentType): ComponentType = {
    components(entity) = component
    component
  }

  def update(entity: Entity, component: ComponentType): Unit = {
    addComponent(entity, component)
  }

  /**
    * Removes the component from the entity if present
    * 
    * @param entity the entity from which to remove the component
    * @return the removed component
    */
  def removeComponent(entity: Entity): Option[Component] = {
    val removed = components.get(entity)
    components -= entity
    removed
  }

  /**
    * Gets the component that is attached to entity
    * 
    * @param entity the entity that may or may not have an attached component
    * @return an option containing the component attached to the entity
    */
  def getComponent(entity: Entity): Option[ComponentType] = {
    components.get(entity) 
  }

  /**
    * Gets all the components managed by this ComponentManager
    * 
    * @return all the components managed by this ComponentManager
    */
  def getAllComponents(): List[ComponentType] = {
    components.values.toList
  }

  /**
    * Gets all the entities that have components managed by this ComponentManager
    * 
    * @return all the entities that have components managed by this ComponentManager
    */
  def getEntities(): List[Entity] = {
    components.keys.toList
  }
}
