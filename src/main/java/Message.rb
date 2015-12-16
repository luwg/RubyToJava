require 'java'

class RubyMessage
  include_class 'Message'
  def sayHello
    return 'hello java'
  end
end
RubyMessage.new