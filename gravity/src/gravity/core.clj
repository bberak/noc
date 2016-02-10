(ns gravity.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [gravity.vectors :as v]))

(defn log [anything]
  (println anything)
  anything)

(defn create-ball [id]
  (let [width (q/width)
        height (q/height)
        mass (q/random 1 5)
        radius (* mass 16)]
    {:location (v/create (q/random 0 (q/width)) (q/random 0 (q/height)))
     :velocity (v/create (q/random -10 10) (q/random -10 10))
     :radius radius
     :max-speed 10
     :mass mass
     :surface-area 2.0
     :id id}))

(defn setup []
  (q/frame-rate 30)
  (let [num-balls 10] 
    {:balls (doall (map (fn [x] (create-ball x)) (range num-balls)))
     :earth {:location (v/create (/ (q/width) 2) (/ (q/height) 2))
             :mass 8
             :radius 128}}))

(defn constrain [val min max]
  (cond (< val min) min (> val max) max :else val))

(defn attract [obj-1 obj-2]
  ;; F = G * m1 * m2 / dist-squared * direction-vector
  (let [dir (v/subtract (:location obj-2) (:location obj-1))
        dist (v/magnitude dir)
        dir-normalized (v/normalize dir)
        gravity 1.0]
    (v/multiply dir-normalized (/ (* gravity (* (:mass obj-1) (:mass obj-2))) (constrain (q/sq dist) 5 25)))))

(defn update-ball [ball balls earth]
  (let [id (:id ball)
        mass (:mass ball)
        max-speed (:max-speed ball)
        gravitational-forces (conj 
                               (map 
                                 (fn [other] (attract ball other)) 
                                 (filter (fn [other] (not= (:id ball) (:id other))) balls))
                               (attract ball earth))
        acceleration (v/divide (apply v/add gravitational-forces) mass)
        new-velocity (v/constrain-magnitude (v/add (:velocity ball) acceleration) max-speed)
        new-location (v/add (:location ball) new-velocity)]
    {:location new-location
     :velocity new-velocity
     :radius (:radius ball)
     :max-speed max-speed
     :mass mass
     :surface-area (:surface-area ball)
     :id id}))

(defn update-state [{balls :balls earth :earth}]
  {:balls (doall (map (fn [ball] (update-ball ball balls earth)) balls))
   :earth earth})

(defn draw-state [{balls :balls earth :earth}]
  (q/background 240)
  (q/fill 50 200 50)
  (q/ellipse 
    (get-in earth [:location :x])
    (get-in earth [:location :y])
    (:radius earth)
    (:radius earth))
  (q/fill 220 30 30)
  (doseq [ball balls]
    (let [location (:location ball)
          radius (:radius ball)]
      (q/ellipse (:x location) (:y location) radius radius))))

(defn -main [] 
  (q/defsketch gravity
    :title "Gravity"
    :size [640 480]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
