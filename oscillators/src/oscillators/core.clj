(ns oscillators.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [oscillators.vectors :as v]
            [oscillators.forces :as f]))

(defn create-oscillator []
  {:angle (v/create 0 0)
   :angular-velocity (v/create (q/random -0.05 0.05) (q/random -0.05 0.05))
   :amplitude (v/create (/ (q/width) 4) (/ (q/height) 4))
   :location (v/create 0 0)})

(defn setup []
  (q/frame-rate 60)
  {:oscillator (create-oscillator)})

(defn update-state [{oscillator :oscillator}]
  (let [new-angle (v/add (:angle oscillator) (:angular-velocity oscillator))
        x (* (get-in oscillator [:amplitude :x]) (q/sin (:x new-angle)))
        y (* (get-in oscillator [:amplitude :y]) (q/sin (:y new-angle)))]
    {:oscillator (assoc oscillator :angle new-angle :location (v/create x y))}))

(defn draw-state [{oscillator :oscillator}]
  (q/background 240)
  (q/fill 50 255 255)
  (let [x (get-in oscillator [:location :x])
        y (get-in oscillator [:location :y])]
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (q/line 0 0 x y)
      (q/ellipse x y 40 40))))

(defn -main []
  (q/defsketch oscillators
    :title "Oscillators"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
