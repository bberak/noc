(ns constant-acceleration.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [constant-acceleration.vectors :as v]))

(defn create-mover []
  {:location {:x (/ (q/width) 2) :y (/ (q/height) 2)}
   :velocity {:x 0 :y 0}
   :acceleration {:x 0.001 :y -0.002}
   :max-speed 10})

(defn reset [{x :x y :y}]
  {:x (cond 
        (> x (q/width)) 0
        (< x 0) (q/width)
        :else x)
   :y (cond 
        (> y (q/height)) 0
        (< y 0) (q/height)
        :else y)})

(defn update-mover [mover]
  (let [location (:location mover)
        max-speed (:max-speed mover)
        velocity (:velocity mover)
        acceleration (:acceleration mover)
        new-velocity (v/limit (v/add velocity acceleration) max-speed)
        new-location (reset (v/add location new-velocity))]
    {:location new-location
     :velocity new-velocity
     :acceleration acceleration
     :max-speed max-speed}))

(defn draw-mover [mover]
  (q/ellipse (get-in mover [:location :x])
             (get-in mover [:location :y])
             50
             50))

(defn setup []
  (q/frame-rate 30)
  {:mover (create-mover)})

(defn update-state [state]
  {:mover (update-mover (:mover state))})

(defn draw-state [state]
  (q/background 240)
  (q/fill 255 0 0)
  (draw-mover (:mover state)))

(defn -main []
  (q/defsketch constant-acceleration
    :title "Constant Acceleration"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
