(ns rocket.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [rocket.vectors :as v]
            [rocket.forces :as f]))

(defn setup []
  (q/frame-rate 60)
  {:rocket {:location (v/create (/ (q/width) 2) (/ (q/height) 2))
            :theta 0}})

(defn read-thrust []
  (if (q/key-pressed?)
    (let [key (q/key-as-keyword)]
      (cond 
        (= key :up) 3.05
        :else 0))
    0))

(defn read-steering []
  (if (q/key-pressed?)
    (let [key (q/key-as-keyword)]
      (cond 
        (= key :right) (q/radians 3.05)
        (= key :left) (q/radians -3.05)
        :else 0))
    0))

(defn update-state [{rocket :rocket}]
  (let [r (read-thrust)
        theta (+ (:theta rocket) (read-steering))
        x (* r (q/cos theta))
        y (* r (q/sin theta))
        location (v/add (:location rocket) (v/create x y))]
    {:rocket {:location location
              :theta theta}}))

(defn draw-state [{rocket :rocket}]
  (q/background 240)
  (q/fill 220 40 40)
  (let [location (:location rocket)
        theta (:theta rocket)]
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [theta]
        (q/triangle -5 -5 -5 5 10 0)))))

(defn -main []
  (q/defsketch rocket
    :title "Rocket"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
