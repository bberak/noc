(ns twod-noise.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/background 255)
  (q/frame-rate 30)
  (q/noise-detail 6)
  {:t 0})

(defn update-state [state]
  {:t (+ (:t state) 1)})

(defn draw-state [state]
  (let [pixels (q/pixels)
        t (:t state)]
    (doseq [x (range 0 (q/width))
            y (range 0 (q/height))]
      (let [brightness (q/map-range (q/noise (* x 0.025) (* y 0.025) (* t 0.025)) 0 1 0 255)]
        (aset-int pixels (+ x (* y (q/width))) (q/color brightness)))))
  (q/update-pixels))

(defn -main []
  (q/defsketch twod-noise
    :title "2D Noise - Smokey"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
