(ns car.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [car.systems :as s]
            [car.renderers :as r]
            [car.entities :as e]
            [org.nfrac.cljbox2d.core :as box]))

(def fps 60)

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 1)
  (let [world (box/new-world [0 -10])
        window {:width 40 :height 30 :center [20 15]}
        vertices (map (fn [x] [x (* (q/noise (/ x 10)) 20)]) (range -1 42))]
    (merge {}
           (e/camera window) 
           (e/world world)
           (e/surface world vertices)
           (e/car world)
           (e/wrecking-ball world (:center window)))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (s/timing (/ 1 fps))
      (s/dragging :left)
      (s/spawning e/flower :left)
      (s/spawning e/cone :right)
      (s/controlling)
      (s/rendering)))

(defn -main []
  (q/defsketch car
    :title "car"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
