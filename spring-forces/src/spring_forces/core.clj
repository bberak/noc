(ns spring-forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [spring-forces.vectors :as v]))

(defn create-spring [anchor rest-length rigidity]
  {:anchor anchor
   :rest-length rest-length
   :rigidity rigidity
   :bob {:location (v/create (:x anchor) (+ (:y anchor) rest-length))
         :mass 8 
         :velocity (v/create 0 0)}})

(defn setup []
  (q/frame-rate 30)
  {:spring (create-spring (v/create (/ (q/width) 2) 75) 350 0.1)})

(defn update-state [state]
  state)

(defn draw-state [{spring :spring}]
  (q/background 240)
  (q/fill 140 140 140)
  (let [anchor (:anchor spring)
        bob (:bob spring)]
    (q/line (:x anchor) (:y anchor) (get-in bob [:location :x]) (get-in bob [:location :y]))
    (q/ellipse (:x anchor) (:y anchor) 35 35)
    (q/ellipse (get-in bob [:location :x]) (get-in bob [:location :y]) (* (:mass bob) 10) (* (:mass bob) 10))))

(defn -main []
  (q/defsketch spring-forces
    :title "Spring Forces"
    :size [640 640]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
