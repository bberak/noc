(ns simple-harmonic-motion.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [simple-harmonic-motion.vectors :as v]
            [simple-harmonic-motion.forces :as f]))

(defn setup []
  (q/frame-rate 30)
  {:color 0
   :angle 0})

(defn update-state [state]
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)})

(defn draw-state [state]
  (q/background 240)
  (q/fill (:color state) 255 255)
  (let [angle (:angle state)
        x (* 150 (q/cos angle))
        y (* 150 (q/sin angle))]
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (q/ellipse x y 100 100))))

(defn -main []
  (q/defsketch simple-harmonic-motion
    :title "You spin my circle right round"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
