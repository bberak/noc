(ns rocket.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [rocket.vectors :as v]
            [rocket.forces :as f]))

(def max-speed 3)

(defn setup []
  (q/frame-rate 60)
  {:rocket {:location (v/create (/ (q/width) 2) (/ (q/height) 2))
            :velocity (v/create 0 0)
            :theta 0
            :mass 5
            :surface-area 1
            :thrusting false}})

(defn read-thrust [theta]
  (if (q/key-pressed?)
    (let [key (q/key-as-keyword)
          mag 0.5
          x (* mag (q/cos theta))
          y (* mag (q/sin theta))]
      (cond 
        (= key :up) (v/create x y)
        :else (v/create 0 0)))
    (v/create 0 0)))

(defn read-steering []
  (if (q/key-pressed?)
    (let [key (q/key-as-keyword)]
      (cond 
        (= key :right) (q/radians 3.05)
        (= key :left) (q/radians -3.05)
        :else 0))
    0))

(defn update-state [{rocket :rocket}]
  (let [location (:location rocket)
        velocity (:velocity rocket)
        theta (:theta rocket)
        mass (:mass rocket)
        surface-area (:surface-area rocket)
        space-density 1.0
        space-drag-coefficient 0.1
        thrust (read-thrust theta) 
        forces (v/add thrust (f/drag rocket {:drag-coefficient space-drag-coefficient :density space-density}))
        acceleration (v/divide forces mass)
        new-velocity (v/constrain-magnitude (v/add acceleration velocity) max-speed)
        new-location (v/add new-velocity location)
        new-theta (+ theta (read-steering))]
    {:rocket {:location new-location
              :velocity new-velocity
              :theta new-theta
              :mass mass
              :surface-area surface-area
              :thrusting (zero? (v/magnitude thrust))}}))

(defn draw-state [{rocket :rocket}]
  (q/background 240)
  (q/fill 220 40 40)
  (let [location (:location rocket)
        theta (:theta rocket)]
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [theta]
        (q/triangle -10 -10 -10 10 15 0)
        (if (true? (:thrusting rocket))
          true
          true)))))

(defn -main []
  (q/defsketch rocket
    :title "Rocket"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
