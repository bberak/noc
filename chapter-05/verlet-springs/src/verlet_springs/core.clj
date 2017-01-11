(ns verlet-springs.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [verlet-springs.systems :as s]
            [verlet-springs.renderers :as r]
            [verlet-springs.entities :as e])
  (:import [toxi.physics2d VerletPhysics2D]
           [toxi.physics2d.behaviors GravityBehavior]
           [toxi.geom Vec2D Rect]))

(def fps 60)

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 1)
  (let [physics (VerletPhysics2D.)]
    (.setWorldBounds physics (Rect. 0 0 (q/width) (q/height)))
    (.addBehavior physics (GravityBehavior. (Vec2D. 0 0.5)))
    (merge {}
           (e/physics physics)
           (e/particle physics (Vec2D. 200 200)))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (s/game-time)
      (s/simple-render)))

(defn -main []
  (q/defsketch verlet-springs
    :title "Verlet Springs"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
