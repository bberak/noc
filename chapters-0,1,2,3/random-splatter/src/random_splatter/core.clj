(ns random-splatter.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [random-splatter.app :as a]))

(q/defsketch random-splatter
  :title "Random Splatter"
  :size [640 480]
  ; setup function called only once, during sketch initialization.
  :setup a/setup
  ; update-state is called on each iteration before draw-state.
  :update a/update-state
  :draw a/draw-state
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
