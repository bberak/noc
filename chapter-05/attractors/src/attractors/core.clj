(ns attractors.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [attractors.systems :as s]
            [attractors.renderers :as r]
            [attractors.entities :as e])
  (:import [toxi.physics2d VerletPhysics2D]
           [toxi.geom Vec2D Rect]))

(def fps 60)

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 1)
  (let [physics (VerletPhysics2D.)
        particles (map 
                    (fn [i]
                      (e/particle physics (.scale (Vec2D/randomVector) (Vec2D. 800 600))))
                    (range 0 20))]
    (.setWorldBounds physics (Rect. 0 0 (q/width) (q/height)))
    (merge {}
           (e/physics physics)
           (e/attractor-particle physics (Vec2D. 400 300))
           (into {} particles))))

(defn prog-loop [entities]
  (-> entities
      (s/game-time)
      (s/rendering)))

(defn -main []
  (q/defsketch attractors
    :title "attractors"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
