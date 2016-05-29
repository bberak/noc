(ns air-and-fluid-resistance.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [air-and-fluid-resistance.vectors :as v]))

(defn log [anything]
  (println anything)
  anything)

(defn create-ball []
  (let [width (q/width)
        height (q/height)
        mass (q/random 1 5)
        radius (* mass 16)]
    {:location (v/create (q/random 0 (q/width)) (q/random 0 0))
     :velocity (v/create (q/random -10 10) (q/random -10 10))
     :radius radius
     :max-speed 10
     :mass mass
     :surface-area 2.0}))

(defn create-liquid []
  {:location (v/create 0 (/ (q/height) 2))
   :bounds {:x {:min 0 :max (q/width)} :y {:min (/ (q/height) 2) :max (q/height)}}
   :drag-coefficient 0.1
   :density 2.0})

(defn setup []
  (q/frame-rate 30)
  {:balls (doall (map (fn [x] (create-ball)) (range 20)))
   :forces {:wind (v/create 0.01 0)
            :gravity (v/create 0 0.1)}
   :liquid (create-liquid)})

(defn drag [ball liquid]
  (if (v/within-bounds? (:location ball) (get-in liquid [:bounds :x]) (get-in liquid [:bounds :y]))
    (let [velocity (:velocity ball)
          drag-coefficient (:drag-coefficient liquid)
          speed (v/magnitude velocity)
          liquid-density (:density liquid)
          surface-area (:surface-area ball)
          normalized-velocity (v/normalize velocity)]
      ;; Simplified drag model: (v/multiply normalized-velocity (* -1 (* drag-coefficient (q/sq speed))))
      (v/multiply normalized-velocity (* (* (* -0.5 (* liquid-density (q/sq speed))) surface-area) drag-coefficient)))
    {:x 0 :y 0}))

(defn update-ball [ball forces liquid]
  (let [mass (:mass ball)
        max-speed (:max-speed ball)
        acceleration (v/divide (v/add 
                                  (:wind forces) 
                                  (v/multiply (:gravity forces) mass)
                                  (drag ball liquid))
                      mass)
        new-velocity (v/constrain-magnitude (v/add (:velocity ball) acceleration) max-speed)
        new-location (v/add (:location ball) new-velocity)
        bounce-results (v/bounce new-location new-velocity)]
    {:location (:location bounce-results)
     :velocity (:velocity bounce-results)
     :radius (:radius ball)
     :max-speed max-speed
     :mass mass
     :surface-area (:surface-area ball)}))

(defn update-state [{balls :balls forces :forces liquid :liquid}]
  {:balls (doall (map (fn [b] (update-ball b forces liquid)) balls))
   :forces forces
   :liquid liquid})

(defn draw-state [{balls :balls liquid :liquid}]
  (q/background 240)
  (q/fill 50 50 200)
  (q/rect
    (get-in liquid [:bounds :x :min])
    (get-in liquid [:bounds :y :min])
    (get-in liquid [:bounds :x :max])
    (get-in liquid [:bounds :y :max]))
  (q/fill 220 30 30)
  (doseq [ball balls]
    (let [location (:location ball)
          radius (:radius ball)]
      (q/ellipse (:x location) (:y location) radius radius))))

(defn -main [] 
  (q/defsketch creating-forces
    :title "Air and fluid resistance"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
