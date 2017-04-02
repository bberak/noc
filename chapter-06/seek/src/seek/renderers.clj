(ns seek.renderers
  (:require [seek.helpers :refer :all]
            [quil.core :as q]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :as box]
            [org.nfrac.cljbox2d.vec2d :as v2]))

(defn astro-body [camera astro-body-components]
  (let [body (:body astro-body-components)
        world-pos (box/position body)
        px-pos (tb/world-to-px camera world-pos)
        radius (:radius astro-body-components)
        scale (tb/world-to-px-scale camera)
        selected (get-in astro-body-components [:selectable :selected])
        stroke (cond (true? selected) [0 255 255] :else [0 0 0])
        stroke-weight (cond (true? selected) 5 :else 1)
        collided (get-in astro-body-components [:collideable :collided])
        color (cond (true? collided) [255 0 0] :else [(- 255 (* 25 radius)) (- 255 (* 25 radius)) (- 255 (* 25 radius))])]
    (q/with-stroke stroke
      (q/stroke-weight stroke-weight)
      (q/with-translation px-pos
        (apply q/fill color)
        (q/ellipse 0 0 (* 2 radius scale) (* 2 radius scale)))))
      (q/stroke-weight 1))

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

(defn particle [particle-components]
  (let [particle (:particle particle-components)]
    (q/fill 251 77 104)
    (q/ellipse (.x particle) (.y particle) 20 20)))

(defn attractor-particle [particle-components]
  (let [particle (:particle particle-components)]
    (q/fill 251 128 158)
    (q/ellipse (.x particle) (.y particle) 40 40)))

(defn spring [spring-components]
  (let [particle-1 (:particle-1 spring-components)
        particle-2 (:particle-2 spring-components)
        x1 (.x particle-1)
        y1 (.y particle-1)
        x2 (.x particle-2)
        y2 (.y particle-2)]
    (q/fill 251 77 104)
    (q/stroke-weight 3)
    (q/line x1 y1 x2 y2)
    (q/ellipse x1 y1 20 20)
    (q/ellipse x2 y2 20 20)))

(defn pendulum [pendulum-components]
  (let [particles (vec (:particles pendulum-components))
        first-particle (first particles)
        last-particle (last particles)
        radius (:radius pendulum-components)]
    (q/fill 255)
    (q/begin-shape)
      (q/curve-vertex (.x first-particle) (.y first-particle))
      (doseq [p particles]
        (q/curve-vertex (.x p) (.y p)))
      (q/curve-vertex (.x last-particle) (.y last-particle))
    (q/end-shape)
    (q/fill 251 77 104)
    (q/ellipse (.x last-particle) (.y last-particle) (* radius 2) (* radius 2))))

(defn cloth [cloth-components]
  (let [particles (:particles cloth-components)]
    (q/fill 255)
    (q/begin-shape :quads)
      (doall (map-indexed 
               (fn [y row]
                  (doall 
                    (map-indexed 
                      (fn [x col]
                        (let [top-left col
                              top-right (get row (inc x))
                              bottom-right (get (get particles (inc y) []) (inc x))
                              bottom-left (get (get particles (inc y) []) x)]
                          (if (every? not-nil? [top-left top-right bottom-right bottom-left])
                            (let []
                              (q/vertex (.x top-left) (.y top-left))
                              (q/vertex (.x top-right) (.y top-right))
                              (q/vertex (.x bottom-right) (.y bottom-right))
                              (q/vertex (.x bottom-left) (.y bottom-left))))))
                      row)))
               particles))
    (q/end-shape)))

(defn graph [graph-components]
  (let [nodes (:nodes graph-components)]
    (q/fill 251 77 104)
    (loop [[head & tail] nodes]
      (if (not-empty tail)
        (let []
          (doseq [other tail]
            (q/line (.x head) (.y head) (.x other) (.y other)))
          (recur tail))))
    (doseq [n nodes]
      (q/ellipse (.x n) (.y n) 20 20))))

(defn particle-with-heading [particle-components]
  (let [particle (:particle particle-components)
        color (:color particle-components)
        velocity (.getVelocity particle)
        heading (q/atan2 (.y velocity) (.x velocity))] ;; q/atan2 expects the y parameter first, then the x
    (apply q/fill color)
    (q/with-translation [(.x particle) (.y particle)]
      (q/with-rotation [heading]
        (q/triangle 34 0 0 9 0 -9)))))

































