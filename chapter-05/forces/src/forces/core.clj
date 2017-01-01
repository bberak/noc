(ns forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [forces.systems :as s]
            [forces.renderers :as r]
            [forces.entities :as e]
            [org.nfrac.cljbox2d.core :as box]))

(def fps 60)

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 1)
  (let [world (box/new-world [0 0])
        window {:width 40 :height 30 :center [20 15]}
        vertices (map (fn [x] [x (* (q/noise (/ x 10)) 20)]) (range -1 42))]
    (merge {}
           (e/camera window) 
           (e/world world)
           (e/astro-body world (:center window) 0.8)
           (e/astro-body world [5 5] 0.2)
           (e/astro-body world [20 20] 0.2)
           (e/astro-body world [17 8] 0.2))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (s/game-time (/ 1 fps))
      (s/selection)
      (s/control)
      (s/spawn e/flower :left)
      (s/spawn e/cone :right)
      (s/gravity)
      (s/render)))

(defn -main []
  (q/defsketch forces
    :title "forces"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
