(ns cloth.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [cloth.systems :as s]
            [cloth.renderers :as r]
            [cloth.entities :as e])
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
           (e/cloth physics (Vec2D. 100 100)))))

(defn prog-loop [entities]
  (q/background 255)
  (-> entities
      (s/game-time)
      (s/drag :left)
      (s/render)))

(defn -main []
  (q/defsketch cloth
    :title "Cloth"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
