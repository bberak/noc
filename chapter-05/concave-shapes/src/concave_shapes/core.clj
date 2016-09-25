(ns concave-shapes.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-ces.core :refer :all]
            [concave-shapes.systems :refer :all]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :refer :all]
            [org.nfrac.cljbox2d.vec2d :refer :all]))

(def fps 60)

(defn render-chain [camera chain-components]
  (q/no-fill)
  (q/begin-shape)
    (doseq [vertex (:vertices chain-components)]
      (let [[x y] (tb/world-to-px camera vertex)]
        (q/curve-vertex x y)))
  (q/end-shape))

(defn render-ball [camera ball-components]
  (let [body (:body ball-components)
        world-pos (position body)
        px-pos (tb/world-to-px camera world-pos)
        radius (:radius ball-components)
        scale (tb/world-to-px-scale camera)]
    (q/fill 175)
    (q/with-translation px-pos
      (q/ellipse 0 0 (* 2 radius scale) (* 2 radius scale)))))

(defn create-ball [world pos]
  (entity {:label :ball
           :renderable render-ball
           :radius 0.5
           :body (body! world {:position pos} {:shape (circle 0.5) :restitution 0.7})}))

(defn render-polygon [camera polygon-components]
  (let [body (:body polygon-components)
        angle (* (angle body) -1)
        world-pos (position body)
        px-pos (tb/world-to-px camera world-pos)
        scale (tb/world-to-px-scale camera)]
    (q/fill 175)
    (q/rect-mode :center)
    (q/with-translation [px-pos]
      (q/with-rotation [angle]
        (q/begin-shape)
          (doseq [[x y] (map 
                           (fn [v] (v-scale v scale)) 
                           (poly-flip-y (:vertices polygon-components)))]
            (q/vertex x y))
        (q/end-shape :close)))))

(defn create-polygon [vertices world pos]
  (let [mapped-vertices (map (fn [v] (v-add v pos)) vertices)]
    (entity {:label :polygon
             :renderable render-polygon
             :vertices vertices
             :body (body! world {:position pos} {:shape (polygon vertices) :restitution 0.7})})))

(defn setup []
  (q/frame-rate fps)
  (q/noise-detail 2)
  (let [camera {:width 40
                :height 30
                :center [20 15]}
        world (new-world [0 -10])
        vertices (map (fn [x] [x (* (q/noise (/ x 5)) 20)]) (range 40))]
    (merge {}
           (entity {:label :camera
                    :camera camera}) 
           (entity {:label :world
                    :world world})
           (entity {:label :chain
                    :renderable render-chain
                    :vertices vertices
                    :body (body! world {:type :static} {:shape (edge-chain vertices) :restitution 0.7})}))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (ticker (/ 1 fps))
      (left-click-and-spawn-ball create-ball)
      (right-click-and-spawn-polygon (partial create-polygon [[1 1] [1 -1] [-1 -1] [-0.5 0.5]]))
      (renderer)))

(defn -main []
  (q/defsketch concave-shapes
    :title "Concave Shapes"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
