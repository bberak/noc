(ns cannon.forces
  (:require [cannon.vectors :as v]
            [quil.core :as q]))

(defn drag [{velocity :velocity surface-area :surface-area} {drag-coefficient :drag-coefficient density :density}]
  (let [speed (v/magnitude velocity)
        normalized-velocity (v/normalize velocity)]
    ;; Simplified drag model: (v/multiply normalized-velocity (* -1 (* drag-coefficient (q/sq speed))))
    (v/multiply normalized-velocity (* (* (* -0.5 (* density (q/sq speed))) surface-area) drag-coefficient))))

(defn friction 
  ([object material] (friction object material 1.0))
  ([{velocity :velocity} {friction-coefficient :friction-coefficient} normal-magnitude]
    (let [normalized-velocity (v/normalize velocity)]
      (v/multiply normalized-velocity (* -1 normal-magnitude friction-coefficient)))))

(defn constrain [val min max]
  (cond (< val min) min (> val max) max :else val))

(defn attract
  ([obj-1 obj-2] (attract obj-1 obj-2 1.0))
  ([obj-1 obj-2 gravity]
    ;;) F = G * m1 * m2 / dist-squared * direction-vector
    (let [dir (v/subtract (:location obj-2) (:location obj-1))
          dist (v/magnitude dir)
          dir-normalized (v/normalize dir)]
      (v/multiply dir-normalized (/ (* gravity (* (:mass obj-1) (:mass obj-2))) (constrain (q/sq dist) 5 25))))))

(defn repel
  ([obj-1 obj-2] (repel obj-1 obj-2 1.0))
  ([obj-1 obj-2 gravity] (v/multiply (attract obj-1 obj-2 gravity) -1)))












