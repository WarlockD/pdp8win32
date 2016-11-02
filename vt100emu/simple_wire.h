#pragma once
#include "global.h"

enum class Level {
	ON, OFF, FALLING, RISING, UNDEFINED
};
inline constexpr Level operator|(Level l, Level r) { 
	return (l == Level::ON || r == Level::ON) ? Level::ON : (l == Level::OFF && r == Level::OFF) ? Level::OFF : Level::UNDEFINED;
}
inline constexpr Level operator&(Level l, Level r) {
	return (l == Level::ON && r == Level::ON) ? Level::ON : (l == Level::OFF || r == Level::OFF) ? Level::OFF : Level::UNDEFINED;
}
inline constexpr Level operator!(Level r) {
	return (r == Level::ON) ? Level::OFF : (r == Level::OFF) ? Level::ON : Level::UNDEFINED;
}
struct Component {
	virtual Level propergate() = 0;
	virtual ~Component() {}
};

struct PortInterface : public Component {
	virtual Level get() const = 0;
	virtual Level set(Level level) = 0;
	virtual ~PortInterface() {}
};

class Port : public PortInterface {
public:
	Port(Level state = Level::OFF) : m_state(state) {}
	virtual Level get() const override { return m_state; }
	virtual Level set(Level state) override {
		assert(state == Level::ON || state == Level::OFF);
		if (state != state) {
			m_state = state == Level::ON ? Level::FALLING : Level::RISING;
		}
		return state;
	}
	virtual Level propergate() override {
		switch (m_state) {
		case Level::FALLING:
			m_state = Level::OFF;
			return Level::FALLING;
		case Level::RISING:
			m_state = Level::ON;
			return Level::RISING;
		default:
			return m_state;
		}
	}
	Level operator()() const { return m_state; }
protected:
	Level m_state;
};

class Wire : public std::vector<PortInterface*> , public Port {
public:
	Wire() {}
	void set(Level state) override {
		Port::set(state);
		if(state == Level::ON)
		for (auto a : *this) a->set(state);
		
		
	}
	virtual Level propergate() override {

	}
};