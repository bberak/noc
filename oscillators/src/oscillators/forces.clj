(ns oscillators.forces
  (:require [oscillators.vectors :as v]
            [quil.core :as q]))

(defn drag [{velocity :velocity surface-area :surface-area location :location} {drag-coefficient :drag-coefficient density :density bounds :bounds}]
  (if (or (or (nil? location) (nil? bounds)) (v/within-bounds? location (:x bounds) (:y bounds)))
    (let [speed (v/magnitude velocity)
          normalized-velocity (v/normalize velocity)]
      ;; Simplified drag model: (v/multiply normalized-velocity (* -1 (* drag-coefficient (q/sq speed))))
      (v/multiply normalized-velocity (* (* (* -0.5 (* density (q/sq speed))) surface-area) drag-coefficient)))
    (v/create 0 0)))

(defn friction 
  ([object material] (friction object material 1.0))
  ([{velocity :velocity location :location} {friction-coefficient :friction-coefficient bounds :bounds} normal-magnitude]
    (if (or (or (nil? location) (nil? bounds)) (v/within-bounds? location (:x bounds) (:y bounds)))
      (let [normalized-velocity (v/normalize velocity)]
        (v/multiply normalized-velocity (* -1 normal-magnitude friction-coefficient)))
      (v/create 0 0))))

(defn constrain [val min max]
  (cond (< val min) min (> val max) max :else val))

(defn gravity
  ([obj-1 obj-2] (gravity obj-1 obj-2 1.0))
  ([obj-1 obj-2 gravitational-strength]
    ;; F = G * m1 * m2 / dist-squared * direction-vector
    (let [dir (v/subtract (:location obj-2) (:location obj-1))
          dist (v/magnitude dir)
          dir-normalized (v/normalize dir)]
      (v/multiply dir-normalized (/ (* gravitational-strength (* (:mass obj-1) (:mass obj-2))) (constrain (q/sq dist) 5 25))))))

(defn anti-gravity
  ([obj-1 obj-2] (anti-gravity obj-1 obj-2 1.0))
  ([obj-1 obj-2 gravitational-strength] (v/multiply (gravity obj-1 obj-2 gravitational-strength) -1)))