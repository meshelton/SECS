package io.github.meshelton.secs.example
import io.github.meshelton.secs._

object Main extends App {

  val entityManager = new EntityManager()

  val velCompMan = new VelocityComponentManager(entityManager)
  val posCompMan = new PositionComponentManager(entityManager)
  val gravCompMan = new GravityComponentManager(entityManager)

  val gravSystem = new GravitySystem(entityManager, gravCompMan, velCompMan, -9.8f)
  gravSystem.updating = true
  val posSystem = new PositionSystem(entityManager, velCompMan, posCompMan)
  posSystem.updating = true

  println("Entity 1 at position (5, 100) with no initial velocity and subject to gravity")
  val entity1 = entityManager()
  posCompMan.addComponent(entity1, PositionComponent(5, 100))
  velCompMan.addComponent(entity1, VelocityComponent(0, 0))
  gravCompMan.addComponent(entity1,GravityComponent())

  println("Entity 2 at position (5, 5) with initial velocity (1, 1)")
  val entity2 = entityManager()
  posCompMan.addComponent(entity2, PositionComponent(5, 5))
  velCompMan.addComponent(entity2, VelocityComponent(1, 1))

  println("Entity 3 subject to gravity")
  val entity3 = entityManager()
  gravCompMan.addComponent(entity3, GravityComponent())

  for( x <- 0 to 10 ){
    entityManager.update(0.3f)

    println("After a 1 second update")
    posCompMan(entity1) match {
      case Some(posComp) => println(s"Entity1 at Pos(${posComp.x}, ${posComp.y})")
      case None => println("Entity1 does not have a position component")
    }

    posCompMan(entity2) match {
      case Some(posComp) => println(s"Entity2 at Pos(${posComp.x}, ${posComp.y})")
      case None => println("Entity2 does not have a position component")
    }

    posCompMan(entity3) match {
      case Some(posComp) => println(s"Entity3 at Pos(${posComp.x}, ${posComp.y})")
      case None => println("Entity3 does not have a position component")
    }
  }
}

class GravitySystem(eM: EntityManager,
                    val gravityCompManager: GravityComponentManager,
                    val velocityCompManager: VelocityComponentManager,
                    val accel: Float) extends System(eM) {
  def update(delta: Float) = {
    gravityCompManager.getEntities().foreach(
      (entity) => {
        for( velComp <- velocityCompManager.getComponent(entity) ){
          velComp.y += accel * delta
        }
      }
    )
  }
}

class PositionSystem(eM: EntityManager,
                     val velocityCompManager: VelocityComponentManager,
                     val positionCompManager: PositionComponentManager ) extends System(eM){

  def update(delta: Float) = {
    positionCompManager.getEntities().foreach(
      (entity) => {
        val posCompOpt = positionCompManager.getComponent(entity)
        val velCompOpt = velocityCompManager.getComponent(entity)

        for( posComp <- posCompOpt ; VelocityComponent(velX, velY) <- velCompOpt ) {
          posComp.y += velY * delta
          posComp.x += velX * delta
        }
      }
    )
  }

}

case class GravityComponent() extends Component

class GravityComponentManager(em: EntityManager) extends ComponentManager[GravityComponent](em) {
  type TypeOfComponent = GravityComponent
}

case class VelocityComponent(var x: Float, var y: Float) extends Component{
  def set(x: Float, y: Float){
    this.x = x
    this.y = y
  }
}

class VelocityComponentManager(em: EntityManager) extends ComponentManager[VelocityComponent](em) {
  type TypeOfComponent = VelocityComponent
}

case class PositionComponent(var x: Float, var y: Float) extends Component {
  def moveTo(x: Float, y: Float) = {
    this.x = x
    this.y = y
  }
  def moveBy(dx: Float, dy: Float) = {
    x += dx
    y += dy
  }
  def getPoint(): (Float, Float) = (x, y)
}

class PositionComponentManager(em: EntityManager) extends ComponentManager[PositionComponent](em) {
  type TypeOfComponent = PositionComponent
}
