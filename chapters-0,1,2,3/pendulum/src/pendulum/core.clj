(ns pendulum.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [pendulum.vectors :as v]))

(def gravity 0.4)

(defn create-pendulum [r angle angular-velocity]
  {:r r
   :angle angle
   :angular-velocity angular-velocity})

(defn setup []
  (q/frame-rate 30)
  (q/stroke 0)
  (q/stroke-weight 2)
  (q/fill 120 120 120)
  {:pendulum (create-pendulum 175 (q/radians 60) 0)})

(defn update-pendulum [p]
  (let [angular-acceleration (/ (* -1 (* gravity (q/sin (:angle p)))) (:r p))
        damping 0.995
        new-angular-velocity (* (+ angular-acceleration (:angular-velocity p)) damping)
        new-angle (+ new-angular-velocity (:angle p))]
    (assoc p :angular-velocity new-angular-velocity :angle new-angle)))

(defn update-state [{p :pendulum}]
  {:pendulum (update-pendulum p)})

(defn draw-state [{p :pendulum}]
  (q/background 240)
  (let [origin (v/create (/ (q/width) 2) 75)
        theta (:angle p)
        r (:r p)
        location (v/add origin
          (v/create (* r (q/sin theta))(* r (q/cos theta))))]
        (q/line (:x origin) (:y origin) (:x location) (:y location))
        (q/ellipse (:x origin) (:y origin) 10 10)
        (q/ellipse (:x location) (:y location) 50 50)))

(defn -main [] 
  (q/defsketch pendulum
    :title "Pendulum"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
