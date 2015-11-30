(ns landscape.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 30)
  (q/noise-detail 4)
  (q/camera  1.5 -251.5 (/ (/ (q/height) 2.0) (Math/tan (/ (* Math/PI 60.0) 360.0))) ; eye
            (/ (q/width) 2.0) (/ (q/height) 2.0) 0 ; centre
            0 1 0) ; up
  {:t 0})

(defn update-state [state]
  {:t (+ (:t state) 1)})

(defn draw-point [x y z scale]
  (q/point (* x scale) (* y scale) (* z scale)))

(defn draw-box [x y z scale]
  (q/push-matrix)
  (q/translate (* x scale) (* y scale) (* z scale))
  (q/box scale)
  (q/pop-matrix))

(defn draw-state [state]
  (q/background 240)
  (let [noise-scale 0.025
        point-scale 5
        t (:t state)]
    (doseq [x (range 0 100)
            z (range 0 50)]
      (let [y (q/map-range (q/noise (* x noise-scale) (* z noise-scale) (* t noise-scale)) 0 1 -25 25)]
        (q/fill (q/map-range y -25 25 255 0) 0 140)
        (draw-box x y z point-scale)))))

(defn -main []
  (q/defsketch landscape
    :title "landscape"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :renderer :opengl
    :middleware [m/fun-mode]))
