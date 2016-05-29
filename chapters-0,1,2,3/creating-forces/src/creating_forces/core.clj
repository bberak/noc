(ns creating-forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [creating-forces.vectors :as v]))

(defn create-ball []
  (let [width (q/width)
        height (q/height)
        mass (q/random 1 5)
        radius (* mass 16)]
    {:location (v/create (q/random 0 width) (q/random 0 height))
     :velocity (v/create (q/random -10 10) (q/random -10 10))
     :radius radius
     :max-speed 10
     :mass mass}))

(defn setup []
  (q/frame-rate 30)
  {:balls (doall (map (fn [x] (create-ball)) (range 20)))
   :forces {:wind (v/create 0.01 0)
            :gravity (v/create 0 0.1)
            :left (v/create -1.95 0)
            :right (v/create 1.95 0)
            :up (v/create 0 -1.95)
            :down (v/create 0 1.95)}})

(defn ease [force ])

(defn update-ball [{location :location velocity :velocity radius :radius max-speed :max-speed mass :mass} 
                   {wind :wind gravity :gravity left :left right :right up :up down :down}]
  (let [acceleration (v/divide (v/add 
                                  wind 
                                  (v/multiply gravity mass) 
                                  (v/multiply up (q/map-range (:y location) 0 (q/height) 0 1))
                                  (v/multiply down (q/map-range (:y location) 0 (q/height) 1 0))
                                  (v/multiply left (q/map-range (:x location) 0 (q/width) 0 1))
                                  (v/multiply right (q/map-range (:x location) 0 (q/width) 1 0))) 
                      mass)
        new-velocity (v/constrain-magnitude (v/add velocity acceleration) max-speed)
        new-location (v/add location new-velocity)
        bounce-results (v/bounce {:location new-location :velocity new-velocity})]
    {:location (:location bounce-results)
     :velocity (:velocity bounce-results)
     :radius radius
     :max-speed max-speed
     :mass mass}))

(defn update-state [{balls :balls forces :forces}]
  {:balls (doall (map (fn [b] (update-ball b forces)) balls))
   :forces forces})

(defn draw-state [{balls :balls}]
  (q/background 240)
  (q/fill 220 30 30)
  (doseq [ball balls]
    (let [location (:location ball)
          radius (:radius ball)]
      (q/ellipse (:x location) (:y location) radius radius))))

(defn -main [] 
  (q/defsketch creating-forces
    :title "Creating Forces"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
