(ns steering.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [steering.systems :as s]
            [steering.renderers :as r]
            [steering.entities :as e])
  (:import [toxi.physics2d VerletPhysics2D]
           [toxi.physics2d.behaviors GravityBehavior]
           [toxi.geom Vec2D Rect]))

(def fps 60)

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 1)
  (let [physics (VerletPhysics2D.)]
    (.setWorldBounds physics (Rect. 0 0 (q/width) (q/height)))
    (merge {}
           (e/physics physics)
           (e/predator physics (Vec2D. 400 100))
           (e/smart-predator physics (Vec2D. 400 500))
           (e/prey physics (Vec2D. 400 300)))))

(defn prog-loop [entities]
  (-> entities
      (s/game-time)
      (s/mouse-input)
      (s/seeking)
      (s/pursuing)
      (s/fleeing)
      (s/rendering)))

(defn -main []
  (q/defsketch steering
    :title "steering"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
