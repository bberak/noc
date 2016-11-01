(ns windmill.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-ces.core :refer :all]
            [windmill.systems :refer :all]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :refer :all]
            [org.nfrac.cljbox2d.vec2d :refer :all]))

(def fps 60)

(defn render-windmill [camera windmill-components]
  (q/fill 100)
  (q/rect-mode :center)
  (let [sail (:sail windmill-components)
        base (:base windmill-components)
        sail-world-pos (position (:sail-body windmill-components))
        sail-angle (* (angle (:sail-body windmill-components)) -1)
        base-world-pos (position (:base-body windmill-components))
        sail-px-pos (tb/world-to-px camera sail-world-pos)
        base-px-pos (tb/world-to-px camera base-world-pos)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation [base-px-pos]
      (q/rect 0 0 (* (:width base) scale) (* (:height base) scale)))
    (q/with-translation [sail-px-pos]
      (q/with-rotation [sail-angle]
        (q/rect 0 0 (* (:width sail) scale) (* (:height sail) scale)))
      (q/ellipse 0 0 10 10))))

(defn create-windmill [world]
  (let [sail {:width 10 :height 1 :position [20 9.5]}
        base {:width 1 :height 10 :position [20 5]}
        sail-body (body! world {:position (:position sail) :type :dynamic} {:shape (box (/ (:width sail) 2) (/ (:height sail) 2)) :restitution 0.7})
        base-body (body! world {:position (:position base) :type :static} {:shape (box (/ (:width base) 2) (/ (:height base) 2)) :restitution 0.7})
        joint (joint! {:type :revolute
                       :body-a base-body
                       :body-b sail-body
                       :world-anchor [20 9.5]
                       :enable-motor true
                       :motor-speed (- PI)
                       :max-motor-torque 10000})]
     (entity {:windmill nil
              :renderable render-windmill
              :sail sail
              :base base
              :sail-body sail-body
              :base-body base-body
              :joint joint})))

(defn render-cone [camera cone-components]
  (let [body (:body cone-components)
        angle (* (angle body) -1)
        world-pos (position body)
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
                           (fn [v] (v-scale v scale)) 
                           (poly-flip-y (:vertices cone-components)))]
          (q/vertex x y))
        (q/end-shape :close)))))

(defn create-cone [world pos]
  (let [radius 0.5
        vertices [[0 -2] [0.5 0] [-0.5 0]]]
    (entity {:cone nil
             :renderable render-cone
             :radius radius
             :vertices vertices
             :body (body! world {:position pos} 
                          {:shape (circle radius) :restitution 0.7}
                          {:shape (polygon vertices) :restitution 0.7})})))

(defn render-flower [camera flower-components]
  (let [body (:body flower-components)
        angle (* (angle body) -1)
        body-pos (position body)
        px-body-pos (tb/world-to-px camera body-pos)
        scale (tb/world-to-px-scale camera)]
    (q/with-translation px-body-pos
      (q/with-rotation [angle]
        (doseq [petal (:petals flower-components)]
          (let [petal-pos (first (poly-flip-y [(:position petal)]))
                px-petal-pos (v-scale petal-pos scale)
                radius (* (:radius petal) scale)
                color (:color petal)]
            (q/with-translation [px-petal-pos]
              (q/fill (nth color 0) (nth color 1) (nth color 2))
              (q/ellipse 0 0 (* 2 radius) (* 2 radius)))))))))

(defn create-flower [world pos]
  (let [petal-color [154 229 125]
        bud-color [232 177 91]
        petals [{:position [0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 0.5] :radius 0.7 :color petal-color}
                {:position [-0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0.5 -0.5] :radius 0.7 :color petal-color}
                {:position [0 0] :radius 0.5 :color bud-color} ;; Flower Bud
                ]]
    (entity {:cone nil
             :renderable render-flower
             :petals petals
             :body (apply body! world {:position pos}
                          (map (fn [x] {:shape (circle (:radius x) (:position x)) :restitution 0.7}) petals))})))

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 2)
  (let [camera {:width 40
                :height 30
                :center [20 15]}
        world (new-world [0 -10])
        vertices (map (fn [x] [x (* (q/noise (/ x 5)) 20)]) (range 40))]
    (merge {}
           (entity {:camera camera}) 
           (entity {:world world})
           (create-windmill world))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (ticker (/ 1 fps))
      (click-and-spawn create-flower :left)
      (click-and-spawn create-cone :right)
      (renderer)))

(defn -main []
  (q/defsketch windmill
    :title "Windmill"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
