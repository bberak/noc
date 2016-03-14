(ns directional-mover.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [directional-mover.vectors :as v]
            [directional-mover.forces :as f]))

(def mover-max-speed 5)
(def mouse-mass 10)

(defn setup []
  (q/frame-rate 30)
  {:mover {:location (v/create 0 0)
           :velocity (v/create 0 0)
           :mass 5}})

(defn update-state [{mover :mover}]
  (let [mouse {:location (v/create (q/mouse-x) (q/mouse-y)) :mass mouse-mass}
        gravity (f/gravity mover mouse)
        acceleration (v/divide gravity (:mass mover))
        new-velocity (v/constrain-magnitude (v/add acceleration (:velocity mover)) mover-max-speed)
        new-location (v/add new-velocity (:location mover))]
    {:mover (assoc mover :location new-location :velocity new-velocity)}))

(defn draw-state [{mover :mover}]
  (q/background 240)
  (q/fill 220 40 40)
  (let [x (get-in mover [:location :x])
        y (get-in mover [:location :y])
        vel-x (get-in mover [:velocity :x])
        vel-y (get-in mover [:velocity :y])
        angle (q/atan2 vel-y vel-x)]
    (q/with-translation [x y]
      (q/with-rotation [angle]
        (q/triangle -5 -5 -5 5 10 0)))))

(defn -main [] 
  (q/defsketch directional-mover
    :title "Directional gravity mover"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
