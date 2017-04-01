(ns seek.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [seek.systems :as s]
            [seek.renderers :as r]
            [seek.entities :as e])
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
           (e/seeker physics (Vec2D. 400 100))
           (e/fleeing-particle physics (Vec2D. 200 100)))))

(defn prog-loop [entities]
  (-> entities
      (s/game-time)
      (s/mouse-input)
      (s/seeking)
      (s/fleeing)
      (s/rendering)))

(defn -main []
  (q/defsketch attractors-2
    :title "attractors-2"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
