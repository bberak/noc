(ns forces-with-friction.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [forces-with-friction.vectors :as v]))

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
   :friction-coefficient 0.21
   :forces {:wind (v/create 0.01 0)
            :gravity (v/create 0 0.1)}})

(defn update-ball [{location :location velocity :velocity radius :radius max-speed :max-speed mass :mass} 
                   {wind :wind gravity :gravity}
                   friction-coefficient]
  (let [normal-magnitude 1.0 ;; Usually, this is calculated based on the object's velocity and gravity
        normalized-velocity (v/normalize velocity)
        friction (v/multiply normalized-velocity (* -1 normal-magnitude friction-coefficient))
        acceleration (v/divide (v/add 
                                  wind 
                                  (v/multiply gravity mass)
                                  friction)
                      mass)
        new-velocity (v/constrain-magnitude (v/add velocity acceleration) max-speed)
        new-location (v/add location new-velocity)
        bounce-results (v/bounce {:location new-location :velocity new-velocity})]
    {:location (:location bounce-results)
     :velocity (:velocity bounce-results)
     :radius radius
     :max-speed max-speed
     :mass mass}))

(defn update-state [{balls :balls forces :forces friction-coefficient :friction-coefficient}]
  {:balls (doall (map (fn [b] (update-ball b forces friction-coefficient)) balls))
   :friction-coefficient friction-coefficient
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
    :title "Forces with friction"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
