(ns pendulum.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [pendulum.vectors :as v]))

(def gravity 8)

(defn create-pendulum [r angle angular-velocity mass]
  {:r r
   :angle angle
   :angular-velocity angular-velocity
   :mass mass})

(defn setup []
  (q/frame-rate 30)
  {:pendulum (create-pendulum 300 (q/radians 60) 0 5)})

(defn update-pendulum [p]
  (let [force (* -1 (* gravity (q/sin (:angle p))))
        angular-acceleration (/ force (:mass p))
        new-angular-velocity (+ angular-acceleration (:angular-velocity p))
        new-angle (+ new-angular-velocity (:angle p))]
    (assoc p :angular-velocity new-angular-velocity :angle new-angular-velocity)))

(defn update-state [{p :pendulum}]
  {:pendulum (update-pendulum p)})

(defn draw-state [{p :pendulum}]
  (q/background 240)
  (q/fill 120 255 255)
  (let [origin (v/create (/ (q/width) 2) 75)
        theta (:angle p)
        r (:r p)]
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
      (q/ellipse x y 100 100))))

(defn -main [] 
  (q/defsketch pendulum
    :title "Pendulum"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
