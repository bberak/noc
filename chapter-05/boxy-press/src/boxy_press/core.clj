(ns boxy-press.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-ces.core :refer :all]
            [boxy-press.systems :refer :all]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :refer :all]
            [org.nfrac.cljbox2d.vec2d :refer :all]))

(def fps 60)

(defn render-ball [camera ball-components]
  (let [body (:body ball-components)
        world-pos (position body)
        px-pos (tb/world-to-px camera world-pos)
        radius (:radius ball-components)
        scale (tb/world-to-px-scale camera)]
    (q/fill 175)
    (q/with-translation px-pos
      (q/ellipse 0 0 (* 2 radius scale) (* 2 radius scale)))))

(defn render-edge [camera edge-components]
  (let [body (:body edge-components)
        vertex-1 (:vertex-1 edge-components)
        vertex-2 (:vertex-2 edge-components)
        [x1 y1] (tb/world-to-px camera vertex-1)
        [x2 y2] (tb/world-to-px camera vertex-2)]
    (q/fill 175)
    (q/line x1 y1 x2 y2)))

(defn create-ball [world pos]
  (entity {:label :ball
           :renderable render-ball
           :radius 1
           :body (body! world {:position pos} {:shape (circle 1) :restitution 0.7})}))

(defn render-box [camera ball-components]
  (let [body (:body ball-components)
        angle (* (angle body) -1)
        world-pos (position body)
        px-pos (tb/world-to-px camera world-pos)
        length (:length ball-components)
        scale (tb/world-to-px-scale camera)]
    (q/fill 175)
    (q/rect-mode :center)
    (q/with-translation px-pos
      (q/with-rotation [angle]
        (q/rect 0 0 (* length scale) (* length scale))))))

(defn create-box [world pos]
  (entity {:label :ball
           :renderable render-box
           :length 2
           :body (body! world {:position pos} {:shape (box 1 1) :restitution 0.7})}))

(defn setup []
  (q/frame-rate fps)
  (let [camera {:width 40
                :height 30
                :center [20 15]}
        world (new-world [0 -10])]
    (merge {}
           (entity {:label :camera
                    :camera camera}) 
           (entity {:label :world
                    :world world})
           (entity {:label :platform
                    :vertex-1 [5 5]
                    :vertex-2 [35 5]
                    :renderable render-edge
                    :body (body! world {:type :static} {:shape (edge [5 5] [35 5])})})
           (entity {:label :platform
                    :vertex-1 [15 15]
                    :vertex-2 [25 15]
                    :renderable render-edge
                    :body (body! world {:type :static} {:shape (edge [15 15] [25 15])})})
           (entity {:label :platform
                    :vertex-1 [30 20]
                    :vertex-2 [40 30]
                    :renderable render-edge
                    :body (body! world {:type :static} {:shape (edge [30 20] [40 30])})})
           (create-box world [25.5 25])
           (create-ball world [14.5 25]))))

(defn prog-loop [entities]
  (q/background 240)
  (-> entities
      (ticker (/ 1 fps))
      (left-click-and-spawn-ball create-ball)
      (right-click-and-spawn-box create-box)
      (renderer)))

(defn -main [] 
  (q/defsketch boxy-press
    :title "Press for boxes!"
    :size [800 600]
    :setup setup
    :update prog-loop
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
