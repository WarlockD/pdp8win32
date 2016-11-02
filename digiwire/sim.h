/***********************************************************************
 * $RCSfile: sim.h,v $		$Revision: 1.21 $
 *
 * $Date: 1998/12/20 06:30:29 $		$Locker:  $
 *
 * --------------------------------------------------------------------
 * DigiTcl 0.3.0 - An Elementary Digital Simulator 
 * (C) 1995-1998 Donald C. Craig (donald@cs.mun.ca)
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * --------------------------------------------------------------------
 *
 * General purpose header file containing typedefs and constants
 * which are useful to the simulator engine as a whole.
 *
 ***********************************************************************/

#if !defined(SIM_H_)		/* protect from multiple inclusion */
#  define SIM_H_
#include <iostream>
#include <sstream>
#include <list>
#include <string>
#include <vector>
#include <chrono>
#include <unordered_map>
#include <vector>
#include <memory>
#include <unordered_set>
#include <set>
#include <cassert>

/*
 * Define our typedefs, signal values and a few useful consts. This header
 * file should be included by most source files in the implementation.
 */


typedef long	ckt_time;

const	ckt_time CKT_TIME_INIT = -1;
const	ckt_time CKT_TIME_NULL = -2;

struct generate_tabs {
	constexpr generate_tabs(int tabs) : tabs(tabs,'\t') {}
	friend std::ostream& operator<<(std::ostream& os, const generate_tabs& gtabs);
	const std::string tabs;
};
std::ostream& operator<<(std::ostream& os, const generate_tabs& gtabs);

class stringtok {
public:
	stringtok() : m_string(""), m_pos(0) {}
	stringtok(const std::string& str) : m_string(str), m_pos(0) {}
	stringtok& operator=(const std::string&str) {
		m_pos = 0;
		m_string = str;
		m_lasttok = "";
		return *this;
	}
	const std::string& operator()(const std::string& delm="\n") {
		m_lasttok.clear();
		size_t find = m_string.find_first_of(delm, m_pos);
		if (find != std::string::npos) {
			m_lasttok = m_string.substr(m_pos, find - 1);
			m_pos = find;
		}
		return m_lasttok;
	}
	const std::string& lasttok() const { return m_lasttok; }
private:
	size_t m_pos;
	std::string m_string;
	std::string m_lasttok;
};
//http://stackoverflow.com/questions/12042824/how-to-write-a-type-trait-is-container-or-is-vector
namespace util {
	template<typename T, typename _ = void>
	struct can_ilterate : std::false_type {};

	template<typename T, typename _ = void>
	struct is_container : std::false_type {};

	template<typename... Ts>
	struct is_container_helper {};

	template<typename T>
	struct can_ilterate<
		T,
		std::conditional_t<
		false,
		is_container_helper<
		decltype(std::declval<T>().begin()),
		decltype(std::declval<T>().end())
		>,
		void
		>
	> : public std::true_type{};

	template<typename T>
	struct is_container<
		T,
		std::conditional_t<
		false,
		is_container_helper<
		typename T::value_type,
		typename T::size_type,
		typename T::allocator_type,
		typename T::iterator,
		typename T::const_iterator,
		decltype(std::declval<T>().size()),
		decltype(std::declval<T>().begin()),
		decltype(std::declval<T>().end()),
		decltype(std::declval<T>().cbegin()),
		decltype(std::declval<T>().cend())
		>,
		void
		>
	> : public std::true_type{};
};
class Port;
class Connector;
class Component; 
// forward defines
#endif	/* !SIM_H_ */
