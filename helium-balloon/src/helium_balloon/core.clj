(ns helium-balloon.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [helium-balloon.vectors :as v]))

(defn create-balloon []
  {:location (v/create (/ (q/width) 2) (q/height))
   :velocity (v/create 0 0)
   :max-speed 7})

(defn setup []
  (q/frame-rate 30)
  {:time 0
   :balloon (create-balloon)
   :forces {:gravity (v/create 0 0.05)
            :wind (v/create 0.01 0)
            :helium (v/create 0 -0.075)}})

(defn update-forces [{gravity :gravity wind :wind helium :helium} time]
  (let [new-wind wind
        new-helium helium]
    {:gravity gravity
     :wind new-wind
     :helium new-helium}))

(defn update-balloon [{location :location velocity :velocity max-speed :max-speed} 
                      {gravity :gravity wind :wind helium :helium}]
  (let [new-acceleration (v/add gravity wind helium)
        new-velocity (v/limit (v/add velocity new-acceleration) max-speed)
        new-location (v/within-bounds (v/add location new-velocity))]
    {:location new-location
     :velocity new-velocity
     :max-speed max-speed}))

(defn update-state [{time :time balloon :balloon forces :forces}]
  (let [new-time (+ 0.01 time)
        new-forces (update-forces forces new-time)
        new-balloon (update-balloon balloon forces)]
    {:time new-time
     :balloon new-balloon
     :forces new-forces}))

(defn draw-state [{balloon :balloon}]
  (q/background 240)
  (q/fill 255 50 50)
  (let [location (:location balloon)]                                  
    (q/ellipse (:x location) (:y location) 50 50)))

(defn -main []
  (q/defsketch helium-balloon
    :title "Helium Balloon"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
