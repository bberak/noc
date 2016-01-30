(ns creating-forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [creating-forces.vectors :as v]))

(defn create-ball []
  (let [width (q/width)
        height (q/height)
        mass (q/random 10 50)
        radius mass]
    {:location (v/create (q/random 0 width) (q/random 0 height))
     :velocity (v/create (q/random -10 10) (q/random -10 10))
     :radius radius
     :max-speed 10
     :mass mass}))

(defn setup []
  (q/frame-rate 30)
  {:balls (doall (map (fn [x] (create-ball)) (range 20)))
   :forces {:wind (v/create 0.01 0)
            :gravity (v/create 0 10.1)}})

(defn scale-gravity [gravity mass]
  (v/multiply gravity mass))

(defn update-ball [{location :location velocity :velocity radius :radius max-speed :max-speed mass :mass} 
                   {wind :wind gravity :gravity}]
  (let [acceleration (v/divide (v/add wind gravity) mass)
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
