(ns interactive-acceleration.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [interactive-acceleration.vectors :as v]))

(defn create-mover []
  {:location {:x (q/random (q/width)) :y (q/random (q/height))}
   :velocity {:x 0 :y 0}
   :acceleration (v/random)
   :max-speed (q/random 3 10)})

(defn create-movers [num]
  (map (fn [n] (create-mover))
       (range 0 num)))

(defn reset [{x :x y :y}]
  {:x (cond 
        (> x (q/width)) 0
        (< x 0) (q/width)
        :else x)
   :y (cond 
        (> y (q/height)) 0
        (< y 0) (q/height)
        :else y)})

(defn update-mover [mover mouse]
  (let [location (:location mover)
        max-speed (:max-speed mover)
        velocity (:velocity mover)
        new-direction (v/subtract mouse location)
        distance (v/magnitude new-direction)
        magnitude-of-acceleration (q/log distance)
        new-acceleration (v/multiply (v/normalize new-direction) magnitude-of-acceleration)
        new-velocity (v/limit (v/add velocity new-acceleration) max-speed)
        new-location (reset (v/add location new-velocity))]
    {:location new-location
     :velocity new-velocity
     :acceleration new-acceleration
     :max-speed max-speed}))

(defn update-movers [movers mouse]
  (map (fn [m] (update-mover m mouse))
       movers))

(defn draw-mover [mover]
  (q/ellipse (get-in mover [:location :x])
             (get-in mover [:location :y])
             50
             50))

(defn setup []
  (q/frame-rate 30)
  {:movers (create-movers 20)})

(defn update-state [state]
  (let [mouse {:x (q/mouse-x) :y (q/mouse-y)}]
    {:movers (update-movers (:movers state) mouse)}))

(defn draw-state [state]
  (q/background 240)
  (q/fill 255 0 0)
  (doseq [m (:movers state)]
    (draw-mover m)))

(defn -main []
  (q/defsketch constant-acceleration
    :title "Random Acceleration"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
