(ns car.renderers
  (:require [quil.core :as q]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :as box]
            [org.nfrac.cljbox2d.vec2d :as v2]))

(defn wrecking-ball [camera wrecking-ball-components]
  (let [body (:body wrecking-ball-components)
        world-pos (box/position body)
        px-pos (tb/world-to-px camera world-pos)
        radius (:radius wrecking-ball-components)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation px-pos
      (q/fill 40 120 160)
      (q/ellipse 0 0 (* 2 radius scale) (* 2 radius scale)))))

(defn car [camera car-components]
  (q/fill 160)
  (q/rect-mode :center)
  (let [chasis (:chasis car-components)
        chasis-body (:chasis-body car-components)
        back-wheel (:back-wheel car-components)
        back-wheel-body (:back-wheel-body car-components)
        front-wheel (:front-wheel car-components)
        front-wheel-body (:front-wheel-body car-components)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation [(tb/world-to-px camera (box/position chasis-body))]
      (q/with-rotation [(* (box/angle chasis-body) -1)]
        (q/rect 0 0 (* (:width chasis) scale) (* (:height chasis) scale))))
    (q/with-translation [(tb/world-to-px camera (box/position back-wheel-body))]
      (q/with-rotation [(* (box/angle back-wheel-body) -1)]
        (q/ellipse 0 0 (* (:radius back-wheel) 2 scale) (* (:radius back-wheel) 2 scale))))
    (q/with-translation [(tb/world-to-px camera (box/position front-wheel-body))]
      (q/with-rotation [(* (box/angle front-wheel-body) -1)]
        (q/ellipse 0 0 (* (:radius front-wheel) 2 scale) (* (:radius front-wheel) 2 scale))))))

(defn windmill [camera windmill-components]
  (q/fill 100)
  (q/rect-mode :center)
  (let [sail (:sail windmill-components)
        base (:base windmill-components)
        sail-world-pos (box/position (:sail-body windmill-components))
        sail-angle (* (box/angle (:sail-body windmill-components)) -1)
        base-world-pos (box/position (:base-body windmill-components))
        sail-px-pos (tb/world-to-px camera sail-world-pos)
        base-px-pos (tb/world-to-px camera base-world-pos)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation [base-px-pos]
      (q/rect 0 0 (* (:width base) scale) (* (:height base) scale)))
    (q/with-translation [sail-px-pos]
      (q/with-rotation [sail-angle]
        (q/rect 0 0 (* (:width sail) scale) (* (:height sail) scale)))
      (q/ellipse 0 0 10 10))))

(defn cone [camera cone-components]
  (let [body (:body cone-components)
        angle (* (box/angle body) -1)
        world-pos (box/position body)
        px-pos (tb/world-to-px camera world-pos)
        radius (:radius cone-components)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation px-pos
      (q/with-rotation [angle]
        (q/fill 251 77 104)
        (q/ellipse 0 0 (* 2 radius scale) (* 2 radius scale))
        (q/fill 232 177 91)
        (q/begin-shape)
        (doseq [[x y] (map 
                           (fn [v] (v2/v-scale v scale)) 
                           (v2/poly-flip-y (:vertices cone-components)))]
          (q/vertex x y))
        (q/end-shape :close)))))

(defn flower [camera flower-components]
  (let [body (:body flower-components)
        angle (* (box/angle body) -1)
        body-pos (box/position body)
        px-body-pos (tb/world-to-px camera body-pos)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation px-body-pos
      (q/with-rotation [angle]
        (doseq [petal (:petals flower-components)]
          (let [petal-pos (first (v2/poly-flip-y [(:position petal)]))
                px-petal-pos (v2/v-scale petal-pos scale)
                radius (* (:radius petal) scale)
                color (:color petal)]
            (q/with-translation [px-petal-pos]
              (q/fill (nth color 0) (nth color 1) (nth color 2))
              (q/ellipse 0 0 (* 2 radius) (* 2 radius)))))))))

(defn surface [camera surface-components]
  (q/no-fill)
  (q/stroke-weight 2)
  (q/begin-shape)
    (doseq [vertex (:vertices surface-components)]
      (let [[x y] (tb/world-to-px camera vertex)]
        (q/curve-vertex x y)))
  (q/end-shape))
