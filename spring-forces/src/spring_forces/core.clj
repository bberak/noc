(ns spring-forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [spring-forces.vectors :as v]))

(defn create-spring [anchor rest-length rigidity]
  {:anchor anchor
   :rest-length rest-length
   :rigidity rigidity
   :bob {:location (v/create (:x anchor) (+ 100 (:y anchor) rest-length))
         :mass 8 
         :velocity (v/create 0 0)}})

(defn setup []
  (q/frame-rate 30)
  (q/stroke 0)
  (q/stroke-weight 2)
  (q/fill 120 120 120)
  {:spring (create-spring (v/create (/ (q/width) 2) 75) 350 0.1)})

(defn update-spring [spring]
  (let [anchor (:anchor spring)
        rigidity (:rigidity spring)
        bob (:bob spring)
        bob-location (:location bob)
        bob-velocity (:velocity bob)
        bob-mass (:mass bob)
        rest-length (:rest-length spring)
        direction (v/subtract bob-location anchor)
        current-length (v/distance anchor bob-location)
        diff (- current-length rest-length)
        spring-force (* -1 rigidity diff)
        force-vector (v/divide (v/multiply (v/normalize direction) spring-force) bob-mass)
        new-velocity (v/add bob-velocity force-vector)
        new-location (v/add bob-location new-velocity)]
    (update-in spring [:bob] assoc :location new-location :velocity new-velocity)))

(defn update-state [{spring :spring}]
  {:spring (update-spring spring)})

(defn draw-state [{spring :spring}]
  (q/background 240)
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
