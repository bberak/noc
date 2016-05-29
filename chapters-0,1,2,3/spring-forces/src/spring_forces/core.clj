(ns spring-forces.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [spring-forces.vectors :as v]))

(def diameter-factor 10)

(defn create-spring [anchor rest-length rigidity]
  {:anchor anchor
   :rest-length rest-length
   :rigidity rigidity
   :clicked false
   :bob {:location (v/create (:x anchor) (+ 100 (:y anchor) rest-length))
         :mass 8 
         :velocity (v/create 0 0)}})

(defn setup []
  (q/frame-rate 60)
  (q/stroke 0)
  (q/stroke-weight 2)
  (q/fill 120 120 120)
  (q/rect-mode :center)
  {:spring (create-spring (v/create (/ (q/width) 2) 75) 350 0.05)
   :gravity (v/create 0 4)})

(defn update-spring [spring gravity]
  (let [anchor (:anchor spring)
        bob (:bob spring)
        bob-location (:location bob)
        bob-velocity (:velocity bob)
        bob-mass (:mass bob)
        rigidity (:rigidity spring)
        rest-length (:rest-length spring)
        direction (v/subtract bob-location anchor)
        current-length (v/magnitude direction)
        stretch (- current-length rest-length)
        spring-force (v/multiply (v/normalize direction) (* -1 rigidity stretch))
        forces (v/divide (v/add spring-force gravity) bob-mass)
        damping 0.99
        new-velocity (v/multiply (v/add bob-velocity forces) damping)
        new-location (v/add bob-location new-velocity)]
    (update-in spring [:bob] assoc :location new-location :velocity new-velocity)))

(defn click-and-drag-bob [spring]
  (let [mouse-pressed (q/mouse-pressed?)
        mouse-vector (v/create (q/mouse-x) (q/mouse-y))
        bob (:bob spring)
        dir (v/subtract (:location bob) mouse-vector)
        bob-radius (/ (* (:mass bob) diameter-factor) 2)
        dist (q/abs (v/magnitude dir))
        clicked-on-bob (< dist bob-radius)]
    (if (and mouse-pressed clicked-on-bob)
      (update-in spring [:bob] assoc :location mouse-vector :velocity (v/create 0 0) :clicked true)
      (update-in spring [:bob] assoc :clicked false))))

(defn update-state [{spring :spring gravity :gravity}]
  (let [updated-spring (-> spring 
                           (update-spring gravity) 
                           click-and-drag-bob)]
    {:spring updated-spring
     :gravity gravity}))

(defn draw-state [{spring :spring}]
  (q/background 240)
  (q/fill 120 120 120)
  (let [anchor (:anchor spring)
        bob (:bob spring)
        bob-diameter (* (:mass bob) diameter-factor)]
    (q/line (:x anchor) (:y anchor) (get-in bob [:location :x]) (get-in bob [:location :y]))
    (q/rect (:x anchor) (:y anchor) 25 25)
    (if (:clicked bob)
      (q/fill 80 80 80))
    (q/ellipse (get-in bob [:location :x]) (get-in bob [:location :y]) bob-diameter bob-diameter)))

(defn -main []
  (q/defsketch spring-forces
    :title "Spring Forces"
    :size [640 640]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
