(ns graph.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [graph.systems :as s]
            [graph.renderers :as r]
            [graph.entities :as e])
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
           (e/graph physics (Vec2D. 200 100)))))

(defn prog-loop [entities]
  (-> entities
      (s/game-time)
      (s/mouse-input)
      (s/dragging)
      (s/clicking)
      (s/rendering)))

(defn -main []
  (q/defsketch graph
    :title "graph"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
