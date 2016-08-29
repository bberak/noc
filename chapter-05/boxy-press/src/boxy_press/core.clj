(ns boxy-press.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [basic-ces.core :refer :all]
            [boxy-press.systems :refer :all]
            [org.nfrac.cljbox2d.testbed :as tb]
            [org.nfrac.cljbox2d.core :refer :all]
            [org.nfrac.cljbox2d.vec2d :refer :all]))

(def fps 60)

(defn render-platform [camera platform-components]
  (let [body (:body platform-components)
        angle (* (angle body) -1)
        world-pos (position body)
        px-pos (tb/world-to-px camera world-pos)
        width (:width platform-components)
        height (:height platform-components)
        scale (tb/world-to-px-scale camera)]
    (q/fill 100)
    (q/rect-mode :center)
    (q/with-translation px-pos
      (q/with-rotation [angle]
        (q/rect 0 0 (* width scale) (* height scale))))))

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
           :radius 1
           :body (body! world {:position pos} {:shape (circle 1) :restitution 0.7})}))

(defn render-box [camera box-components]
  (let [body (:body box-components)
        angle (* (angle body) -1)
        world-pos (position body)
        px-pos (tb/world-to-px camera world-pos)
        length (:length box-components)
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
                    :renderable render-platform
                    :width 10
                    :height 0.5
                    :body (body! world {:position [30 25] :type :static :angle (q/radians 45)} {:shape (box 5 0.25) :restitution 0.7})})
           
           (entity {:label :platform
                    :renderable render-platform
                    :width 10
                    :height 0.5
                    :body (body! world {:position [20 15] :type :static} {:shape (box 5 0.25) :restitution 0.7})})
           
           (entity {:label :platform
                    :renderable render-platform
                    :width 30
                    :height 0.5
                    :body (body! world {:position [20 5] :type :static} {:shape (box 15 0.25) :restitution 0.7})})
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
