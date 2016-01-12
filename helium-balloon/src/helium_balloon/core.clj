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
            :wind (v/create 0.0 0.0)
            :helium (v/create 0 -0.075)}})

(defn update-helium [time]
  ;; http://tube.geogebra.org/material/simple/id/1672433#material/1672471
  ;; y = a * (1 + r / t) ^ [t * (x - h)] + k
  (let [a -0.53 r -0.08 t 4 x time h 2 k 0]
    {:x 0
     :y (+ (* a (Math/pow (+ 1 (/ r t)) (* t (- x h)))) k)}))

(defn update-wind [time wind-strength]
  {:x (q/map-range (q/noise time) 0 1 (- wind-strength) wind-strength)
   :y (q/map-range (q/noise (+ time 10000)) 0 1 (- wind-strength) wind-strength)})

(defn update-forces [{gravity :gravity wind :wind helium :helium} time]
  (let [wind-strength 0.45
        new-wind (update-wind time wind-strength)
        new-helium (update-helium time)]
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

(defn draw-state [{balloon :balloon forces :forces} ]
  (q/background 240)
  (q/fill 255 50 50)
  (let [location (:location balloon)]                                  
    (q/ellipse (:x location) (:y location) 50 50))
  (let [wind (:wind forces)
        wind-mag (v/magnitude wind)
        wind-norm (v/normalize wind)
        wind-scaled (v/multiply wind 50)]
    (q/with-translation [(/ (q/width) 2) (/ (q/height) 2)]
      (q/line 0 0 (:x wind-scaled) (:y wind-scaled)))))

(defn -main []
  (q/defsketch helium-balloon
    :title "Helium Balloon"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
