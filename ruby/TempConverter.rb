class TempConverter
  def c2f(degrees)
    degrees*9.0/5.0+32
  end

  def f2c(degrees)
    (degrees-32)*5.0/9.0
  end
end

def getTempConverter
  TempConverter.new
end