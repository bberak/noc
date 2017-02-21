(ns attractors-2.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [attractors-2.systems :as s]
            [attractors-2.renderers :as r]
            [attractors-2.entities :as e])
  (:import [toxi.physics2d VerletPhysics2D]
           [toxi.physics2d.behaviors GravityBehavior]
           [toxi.geom Vec2D Rect]))

(def fps 60)

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 1)
  (let [physics (VerletPhysics2D.)]
    (.setWorldBounds physics (Rect. 0 0 (q/width) (q/height)))
    ;;(.addBehavior physics (GravityBehavior. (Vec2D. 0 0.5)))
    (merge {}
           (e/physics physics)
           (e/graph physics (Vec2D. 200 100))
           ;;(e/pendulum physics (Vec2D. 400 100))
           )))

(defn prog-loop [entities]
  (-> entities
      (s/game-time)
      (s/mouse-input)
      (s/rendering)))

(defn -main []
  (q/defsketch attractors-2
    :title "attractors-2"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
