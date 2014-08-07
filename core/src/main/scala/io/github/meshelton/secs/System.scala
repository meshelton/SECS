package io.github.meshelton.secs

/**
  * A system contains the logic manipulating data in components attached to entities
  * 
  * Subclasses of this class should take as constructor parameters all the [[io.github.meshelton.secs.ComponentManager ComponentManagers]] that it is interested in.
  * 
  * @constructor creates a new system registered to the [[io.github.meshelton.secs.EntityManager EntityManager]]
  * @param entityManager the EntityManager that this system is registered to
  */
trait System {
  implicit val entityManager: EntityManager
  /**
    * Should this System be updating
    */
  var updating = false

  entityManager.registerSystem(this)

  def update(delta: Float)
}
